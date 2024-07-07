package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;

public class ParameterizedPredicate<T extends IPlayerElement> implements IPredicate<T> {

    protected final IVarTriFunction<State, T, Object, Result<String>> predicate;

    public ParameterizedPredicate(IVarTriFunction<State, T, Object, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public Result<String> test(State state, T t, Object... meta) {
        return predicate.accept(state, t, meta);
    }

    public Condition<T> toCondition() {
        return new Condition<>(this);
    }

}
