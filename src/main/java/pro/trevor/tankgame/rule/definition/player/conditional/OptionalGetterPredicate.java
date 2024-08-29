package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class OptionalGetterPredicate<T> extends RulePredicate {
    public OptionalGetterPredicate(BiFunction<State, PlayerRef, Optional<T>> getter, IVarTriPredicate<State, Optional<T>, Object> predicate, String message) {
        super((state, playerRef, meta) -> predicate.test(state, getter.apply(state, playerRef), meta), message);
    }

    public OptionalGetterPredicate(BiFunction<State, PlayerRef, Optional<T>> getter, BiPredicate<State, Optional<T>> predicate, String message) {
        super((state, playerRef, meta) -> predicate.test(state, getter.apply(state, playerRef)), message);
    }
}
