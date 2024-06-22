package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

public class Predicate<T extends IPlayerElement> {

    protected final IVarTriFunction<State, T, Object, Result<String>> predicate;

    public Predicate(IVarTriFunction<State, T, Object, Result<String>> predicate) {
        this.predicate = predicate;
    }

    public Result<String> test(State state, T t, Object... meta) {
        return predicate.accept(state, t, meta);
    }

    public Condition<T> toCondition() {
        return new Condition<>(this);
    }

}
