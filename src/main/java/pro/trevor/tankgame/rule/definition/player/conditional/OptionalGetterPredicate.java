package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.Optional;
import java.util.function.BiFunction;

public class OptionalGetterPredicate<T> extends RulePredicate {
    public OptionalGetterPredicate(BiFunction<State, PlayerRef, Optional<T>> getter, IVarTriPredicate<State, Optional<T>, Object> predicate, String message) {
        super((s, p, n) -> predicate.test(s, getter.apply(s, p), n), message);
    }
}
