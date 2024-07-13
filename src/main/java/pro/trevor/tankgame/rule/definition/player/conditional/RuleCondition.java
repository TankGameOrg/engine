package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;

import java.util.ArrayList;
import java.util.List;

public class RuleCondition {

    private final RulePredicate[] predicates;

    public RuleCondition(RulePredicate... predicates) {
        this.predicates = predicates;
    }

    public Result<List<String>> test(State state, PlayerRef t, Object... meta) {
        List<String> errors = new ArrayList<>();

        for (RulePredicate predicate : predicates) {
            Result<String> error = predicate.test(state, t, meta);
            if (error.isError()) {
                errors.add(error.getError());
            }
        }

        if (errors.isEmpty()) {
            return Result.ok();
        } else {
            return Result.error(errors);
        }
    }
}
