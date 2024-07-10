package pro.trevor.tankgame;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerActionRule;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.definition.range.VariableTypeRange;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;

import java.util.*;

import org.json.*;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Pair;

public class Api {
    private final Ruleset ruleset;
    private State state;

    private static final String COUNCIL = "Council";

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
        if (!Attribute.RUNNING.fromOrElse(state, true)) {
            throw new Error("The game is over; no actions can be submitted");
        }
        else if (json.keySet().contains(JsonKeys.DAY)) {
            applyTick(state, ruleset);
        } else {
            JSONObject subject = json.getJSONObject(JsonKeys.SUBJECT);
            String action = json.getString(JsonKeys.ACTION);
            long time = json.optLong(JsonKeys.TIME, 0);

            Optional<Pair<Class<?>, IPlayerRule<?>>> optionalRule = ruleset.getPlayerRules().getByName(action);
            if (optionalRule.isEmpty()) {
                throw new Error("Unexpected action: " + action);
            }

            Pair<Class<?>, IPlayerRule<?>> rulePair = optionalRule.get();
            Class<?> ruleClass = rulePair.left();
            IPlayerRule<Object> rule = (IPlayerRule<Object>) rulePair.right();

            Object decodedSubject = decodeJsonAndHandlePlayerRef(subject);

            try {
                if (rule instanceof TimedPlayerActionRule) {
                    rule.apply(state, ruleClass.cast(decodedSubject), getArgumentsTimed(rule, json, time));
                } else {
                    rule.apply(state, ruleClass.cast(decodedSubject), getArguments(rule, json));
                }
            } catch (ClassCastException e) {
                throw new Error("Unable to cast given subject to class " + ruleClass.getSimpleName(), e);
            }
        }

        enforceInvariants(state, ruleset);
        applyConditionals(state, ruleset);
    }

    private Object decodeJsonAndHandlePlayerRef(JSONObject json) {
        Object decodedJson = Codec.decodeJson(json);
        if (decodedJson instanceof PlayerRef playerRef) {
            return ruleset.getPlayerObject(state, playerRef);
        } else {
            return decodedJson;
        }
    }

    private Object[] getArguments(IPlayerRule<?> rule, JSONObject json) {
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

    private Object[] getArgumentsTimed(IPlayerRule<?> rule, JSONObject json, long time) {
        Object[] args = getArguments(rule, json);
        Object[] out = new Object[args.length + 1];

        System.arraycopy(args, 0, out, 1, args.length);
        out[0] = time;
        return out;
    }

    public JSONObject getPossibleActions(PlayerRef player) {
        JSONObject actions = new JSONObject();
        actions.put("error", false);
        actions.put("type", "possible_actions");
        actions.put("player", player);

        JSONArray actionsArray = new JSONArray();
        List<IPlayerRule<?>> rules;
        Class<?> type;
        Object subject;

        if (player.getName().equals(COUNCIL) || state.getCouncil().isPlayerOnCouncil(player)) {
            type = Council.class;
            rules = ruleset.getPlayerRules().get(type);
            subject = state.getCouncil();
        } else if (state.getPlayer(player).isPresent()) {
            type = GenericTank.class;
            rules = ruleset.getPlayerRules().get(type);
            Optional<GenericTank> tank = state.getBoard().gather(GenericTank.class).stream()
                    .filter((t) -> t.getPlayerRef().equals(player))
                    .findFirst();
            if (tank.isPresent()) {
                subject = tank.get();
            } else {
                actions.put("actions", actionsArray);
                return actions;
            }
        } else {
            throw new Error("Unknown player: " + player);
        }

        assert type.isInstance(subject);

        for (IPlayerRule<?> rule : rules) {
            JSONObject actionJson = new JSONObject();
            actionJson.put("rule", rule.name());
            actionJson.put("subject", type.getSimpleName().toLowerCase());

            // find all states of each parameter
            JSONArray fields = new JSONArray();
            for (TypeRange<?> field : rule.parameters()) {
                if (field instanceof VariableTypeRange<?,?> variableField) {
                    VariableTypeRange<Object, ?> genericField = (VariableTypeRange<Object, ?>) variableField;
                    genericField.generate(state, subject);
                }
                fields.put(field.toJson());
            }
            actionJson.put("fields", fields);
            actionsArray.put(actionJson);
        }

        actions.put("actions", actionsArray);
        return actions;
    }

    private static void enforceInvariants(State state, Ruleset ruleset) {
        state.gatherAll().forEach((x) -> ruleset.getEnforcerRules().enforceRules(state, x));
    }

    private static void applyConditionals(State state, Ruleset ruleset) {
        state.gatherAll().forEach((x) -> ruleset.getConditionalRules().applyRules(state, x));
    }

    private static void applyTick(State state, Ruleset ruleset) {
        state.gatherAll().forEach((x) -> ruleset.getTickRules().applyRules(state, x));
    }

    private static class JsonKeys {
        public static final String DAY = "day";
        public static final String SUBJECT = "subject";
        public static final String ACTION = "action";
        public static final String TIME = "timestamp";
    }
}
