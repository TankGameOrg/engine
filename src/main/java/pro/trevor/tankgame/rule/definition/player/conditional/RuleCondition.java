package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.util.Result;

import java.util.ArrayList;
import java.util.List;

public class RuleCondition {

    private final IRulePredicate[] predicates;

    public RuleCondition(IRulePredicate... predicates) {
        this.predicates = predicates;
    }

    public List<PlayerRuleError> test(PlayerRuleContext context) {
        List<PlayerRuleError> errors = new ArrayList<>();

        for (IRulePredicate predicate : predicates) {
            Result<Void, PlayerRuleError> error = predicate.test(context);
            if (error.isError()) {
                errors.add(error.getError());
            }
        }

        return errors;
    }
}
