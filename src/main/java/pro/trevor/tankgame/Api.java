package pro.trevor.tankgame;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.actions.PossibleAction;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
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

    public List<PossibleAction> getPossibleActions(PlayerRef subject) {
        List<PossibleAction> actions = new ArrayList<>();
        List<IPlayerRule> rules = ruleset.getPlayerRules().getAllRules();

        if (state.getPlayer(subject).isEmpty()) {
            throw new Error("Unknown player: " + subject);
        }

        for (IPlayerRule rule : rules) {
            // Check if the rule is applicable to this (state, player) combination
            PlayerRuleContext context = new PlayerRuleContext(state, subject);
            List<PlayerRuleError> canApplyErrors = rule.canApply(context);

            List<PlayerRuleError> errors = canApplyErrors.stream()
                // Remove any errors that the client shouldn't see i.e. insufficent data
                .filter((error) -> !errorsToFilterOut.contains(error.getCategory()))
                .toList();

            // If this action is not applicable to the current player don't send it to UI
            if(canApplyErrors.stream().filter((error) -> error.getCategory() == PlayerRuleError.Category.NOT_APPLICABLE).findAny().isPresent()) {
                continue;
            }

            // find all states of each parameter if the rule can be applied
            List<LogFieldSpec<?>> fields = errors.isEmpty() ?
                rule.getFieldSpecs(context) :
                List.of();

            actions.add(new PossibleAction(rule.name(), errors, fields));
        }

        return actions;
    }
}
