package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

public class RulePredicateWithMeta implements IRulePredicate {

    protected final IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate;

    public RulePredicateWithMeta(IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public RulePredicateWithMeta(IVarTriPredicate<State, PlayerRef, Object> predicate, String message) {
        this.predicate = (state, t, v) -> predicate.test(state, t, v) ? Result.ok() : Result.error(message);
    }

    public Result<String> test(State state, PlayerRef t, Object... meta) {
        return predicate.accept(state, t, meta);
    }

    public Result<String> test(State state, PlayerRef t) {
        // We don't have metadata so we assume that the predicate passes
        return Result.ok();
    }

    public RuleCondition toCondition() {
        return new RuleCondition(this);
    }

}
