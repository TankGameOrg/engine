package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

public class Predicate<T extends IPlayerElement> {

    protected final IVarTriPredicate<State, T, Object> predicate;
    protected final IVarTriFunction<State, T, Object, String> errorFunction;

    public Predicate(IVarTriPredicate<State , T, Object> predicate, String errorMessage) {
        this.predicate = predicate;
        this.errorFunction = (s, t, n) -> errorMessage;
    }

    public Predicate(IVarTriPredicate<State , T, Object> predicate, IVarTriFunction<State, T, Object, String>  errorFunction) {
        this.predicate = predicate;
        this.errorFunction = errorFunction;
    }

    public Result<String> test(State state, T t, Object... meta) {
        if (predicate.test(state, t, meta)) {
            return Result.ok();
        } else {
            return Result.error(errorFunction.accept(state, t, meta));
        }
    }

}
