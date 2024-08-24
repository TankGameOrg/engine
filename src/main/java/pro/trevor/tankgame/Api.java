package pro.trevor.tankgame;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerConditionRule;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.definition.range.VariableTypeRange;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.board.unit.GenericTank;

import java.util.*;

import org.json.*;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;

public class Api {
    private final Ruleset ruleset;
    private State state;

    public Api(IRulesetRegister ruleset) {
        this.ruleset = IRulesetRegister.getRuleset(ruleset);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public JSONObject getRules() {
        JSONObject rules = new JSONObject();
        rules.put("type", "rules");
        rules.put("rules", ruleset.getPlayerRules().toJsonRequirements());
        return rules;
    }

    public void ingestAction(JSONObject json) {
        if (!state.getOrElse(Attribute.RUNNING, true)) {
            System.out.println(state);
            throw new Error("The game is over; no actions can be submitted");
        }
        else if (json.keySet().contains(JsonKeys.DAY)) {
            ruleset.getTickRules().applyRules(state);
        } else {
            JSONObject subject = json.getJSONObject(JsonKeys.SUBJECT);
            String action = json.getString(JsonKeys.ACTION);
            long time = json.optLong(JsonKeys.TIME, 0);

            Optional<IPlayerRule> optionalRule = ruleset.getPlayerRules().getByName(action);
            if (optionalRule.isEmpty()) {
                throw new Error("Unexpected action: " + action);
            }

            IPlayerRule rule = optionalRule.get();

            Object decodedSubject = Codec.decodeJson(subject);
            if (decodedSubject instanceof PlayerRef player) {
                if (rule instanceof TimedPlayerConditionRule) {
                    rule.apply(state, player, getArgumentsTimed(rule, json, time));
                } else {
                    rule.apply(state, player, getArguments(rule, json));
                }
            } else {
                throw new Error("Subject is not a PlayerRef" + decodedSubject.getClass().getSimpleName());
            }
        }

        ruleset.getEnforcerRules().enforceRules(state);
        ruleset.getConditionalRules().applyRules(state);
    }

    private Object decodeJsonAndHandlePlayerRef(JSONObject json) {
        Object decodedJson = Codec.decodeJson(json);
        if (decodedJson instanceof PlayerRef playerRef) {
            return state.getBoard().gatherUnits(GenericTank.class).stream().filter((t) -> t.getPlayerRef().equals(playerRef)).findFirst().orElse(null);
        } else {
            return decodedJson;
        }
    }

    private Object[] getArguments(IPlayerRule rule, JSONObject json) {
        Object[] arguments = new Object[rule.parameters().length];
        for (int i = 0; i < arguments.length; ++i) {
            Object input = json.get(rule.parameters()[i].getName());
            if (input instanceof JSONObject inputJson) {
                arguments[i] = decodeJsonAndHandlePlayerRef(inputJson);
            } else {
                // Try to assume that, if it is not JSON-encoded object, it is a primitive
                arguments[i] = input;
            }
        }
        return arguments;
    }

    private Object[] getArgumentsTimed(IPlayerRule rule, JSONObject json, long time) {
        Object[] args = getArguments(rule, json);
        Object[] out = new Object[args.length + 1];

        System.arraycopy(args, 0, out, 1, args.length);
        out[0] = time;
        return out;
    }

    public JSONObject getPossibleActions(PlayerRef subject) {
        JSONObject actions = new JSONObject();
        actions.put("error", false);
        actions.put("type", "possible_actions");
        actions.put("player", subject);

        JSONArray actionsArray = new JSONArray();
        List<IPlayerRule> rules = ruleset.getPlayerRules().getAllRules();

        if (state.getPlayer(subject).isEmpty()) {
            throw new Error("Unknown player: " + subject);
        }

        for (IPlayerRule rule : rules) {
            JSONObject actionJson = new JSONObject();
            actionJson.put("rule", rule.name());

            // Check if the rule is applicable to this (state, player) combination
            Result<List<String>> canApplyResult = Result.ok();
            if (rule instanceof PlayerConditionRule conditionRule) {
                canApplyResult = conditionRule.canApplyConditionalWithoutMeta(state, subject);
            }

            if(canApplyResult.isOk()) {
                actionJson.put("errors", new JSONArray());
            }
            else {
                actionJson.put("errors", new JSONArray(canApplyResult.getError()));
            }

            // find all states of each parameter if the rule can be applied
            JSONArray fields = new JSONArray();
            if(canApplyResult.isOk()) {
                for (TypeRange<?> field : rule.parameters()) {
                    if (field instanceof VariableTypeRange<?,?> variableField) {
                        VariableTypeRange<Object, ?> genericField = (VariableTypeRange<Object, ?>) variableField;
                        genericField.generate(state, subject);
                    }
                    fields.put(field.toJson());
                }
            }
            actionJson.put("fields", fields);
            actionsArray.put(actionJson);
        }

        actions.put("actions", actionsArray);
        return actions;
    }

    private static class JsonKeys {
        public static final String DAY = "day";
        public static final String SUBJECT = "subject";
        public static final String ACTION = "action";
        public static final String TIME = "timestamp";
    }
}
