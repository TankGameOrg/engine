package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class RulePredicate {

    protected final IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate;
    private final boolean isCheckable;

    public RulePredicate(IVarTriFunction<State, PlayerRef, Object, Result<String>> predicate) {
        this.predicate = predicate;
        this.isCheckable = false;
    }

    public RulePredicate(IVarTriPredicate<State, PlayerRef, Object> predicate, String message) {
        this((state, player, n) -> predicate.test(state, player, n) ? Result.ok() : Result.error(message));
    }

    public RulePredicate(BiFunction<State, PlayerRef, Result<String>> getter) {
        this.predicate = (state, player , n) -> getter.apply(state, player);
        this.isCheckable = true;
    }

    public RulePredicate(BiPredicate<State, PlayerRef> getter, String message) {
        this.predicate = (state, player , n) -> getter.test(state, player) ? Result.ok() : Result.error(message);
        this.isCheckable = true;
    }

    public boolean isCheckable() {
        return isCheckable;
    }

    public Result<String> test(State state, PlayerRef player, Object... meta) {
        return predicate.accept(state, player, meta);
    }

    public RuleCondition toCondition() {
        return new RuleCondition(this);
    }

}
