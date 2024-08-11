package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;

public class RulePredicate implements IRulePredicate {

    protected final BiFunction<State, PlayerRef, Result<String>> predicate;

    public RulePredicate(BiFunction<State, PlayerRef, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public RulePredicate(BiPredicate<State, PlayerRef> predicate, String message) {
        this.predicate = (state, t) -> predicate.test(state, t) ? Result.ok() : Result.error(message);
    }

    public Result<String> test(State state, PlayerRef t, Object... meta) {
        return predicate.apply(state, t);
    }

    public Result<String> test(State state, PlayerRef t) {
        return predicate.apply(state, t);
    }

    public RuleCondition toCondition() {
        return new RuleCondition(this);
    }

}
