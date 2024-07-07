package pro.trevor.tankgame.rule.definition.player;

import java.util.function.BiFunction;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;

public class UnparameterizedPredicate<T extends IPlayerElement> implements IPredicate<T> {

    protected final BiFunction<State, T, Result<String>> predicate;

    public UnparameterizedPredicate(BiFunction<State, T, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public Result<String> test(State state, T t, Object... meta) {
        return predicate.apply(state, t);
    }

    public Result<String> test(State state, T t) {
        return predicate.apply(state, t);
    }

    public Condition<T> toCondition() {
        return new Condition<>(this);
    }

}
