package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

public class RulePredicate {

    protected final IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate;

    public RulePredicate(IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public RulePredicate(IVarTriPredicate<State, PlayerRef, Object> predicate, String message) {
        this.predicate = (state, t, v) -> predicate.test(state, t, v) ? Result.ok() : Result.error(message);
    }

    public Result<String> test(State state, PlayerRef t, Object... meta) {
        return predicate.accept(state, t, meta);
    }

    public RuleCondition toCondition() {
        return new RuleCondition(this);
    }

}
