package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleCondition {

    private final IRulePredicate[] predicates;

    public RuleCondition(IRulePredicate... predicates) {
        this.predicates = predicates;
    }

    public List<PlayerRuleError> test(PlayerRuleContext context) {
        List<PlayerRuleError> errors = new ArrayList<>();

        for (IRulePredicate predicate : predicates) {
            Optional<PlayerRuleError> error = predicate.test(context);
            if (error.isPresent()) {
                errors.add(error.get());
            }
        }

        return errors;
    }
}
