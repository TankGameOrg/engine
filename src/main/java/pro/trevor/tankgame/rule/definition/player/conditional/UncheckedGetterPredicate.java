package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.function.BiFunction;

public class UncheckedGetterPredicate<T extends AttributeContainer> extends RulePredicate {
    public UncheckedGetterPredicate(BiFunction<State, PlayerRef, T> getter, IVarTriPredicate<State, T, Object> predicate, String message) {
        super((state, playerRef) -> predicate.test(state, getter.apply(state, playerRef)), message);
    }
}
