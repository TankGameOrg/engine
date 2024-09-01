package pro.trevor.tankgame;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.definition.range.VariableTypeRange;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;

import java.util.*;

import org.json.*;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class Api {
    private final Ruleset ruleset;
    private State state;

    // A list of errors that should not be send back to the client when requesting possible actions
    private static final Set<PlayerRuleError.Category> errorsToFilterOut = Set.of(
        PlayerRuleError.Category.INSUFFICIENT_DATA
    );

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
        LogEntry logEntry = new LogEntry(json);

        if (!state.getOrElse(Attribute.RUNNING, true)) {
            System.out.println(state);
            throw new Error("The game is over; no actions can be submitted");
        }
        else if (logEntry.has(Attribute.DAY)) {
            ruleset.getTickRules().applyRules(state);
        } else {
            PlayerRef subject = logEntry.getUnsafe(Attribute.SUBJECT);
            String action = logEntry.getUnsafe(Attribute.ACTION);

            Optional<IPlayerRule> optionalRule = ruleset.getPlayerRules().getByName(action);
            if (optionalRule.isEmpty()) {
                throw new Error("Unexpected action: " + action);
            }

            IPlayerRule rule = optionalRule.get();
            PlayerRuleContext context = new PlayerRuleContext(state, subject, logEntry);
            rule.apply(context);
        }

        ruleset.getEnforcerRules().enforceRules(state);
        ruleset.getConditionalRules().applyRules(state);
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
            List<PlayerRuleError> canApplyErrors = rule.canApply(new PlayerRuleContext(state, subject));

            List<JSONObject> jsonErrors = canApplyErrors.stream()
                // Remove any errors that the client shouldn't see i.e. insufficent data
                .filter((error) -> !errorsToFilterOut.contains(error.getCategory()))
                .map((error) -> {
                    JSONObject jsonError = new JSONObject();
                    jsonError.put("category", error.getCategory().toString());
                    jsonError.put("message", error.getMessage());

                    Optional<Long> errorExpiration = error.getErrorExpirationTime();
                    if(errorExpiration.isPresent()) {
                        jsonError.put("expiration", errorExpiration.get());
                    }

                    return jsonError;
                })
                .toList();

            // If any actions are not applicable to the current player don't send them to UI
            if(canApplyErrors.stream().filter((error) -> error.getCategory() == PlayerRuleError.Category.NOT_APPLICABLE).findAny().isPresent()) {
                continue;
            }

            // If canApply didn't return any errors or all of they get filtered out this will be an empty array aka no error
            actionJson.put("errors", new JSONArray(jsonErrors));

            // find all states of each parameter if the rule can be applied
            JSONArray fields = new JSONArray();
            if(jsonErrors.isEmpty()) {
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
}
