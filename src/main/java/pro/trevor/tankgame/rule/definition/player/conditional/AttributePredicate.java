package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class AttributePredicate<T extends AttributeContainer> extends GetterPredicate<T> {
    public AttributePredicate(BiFunction<State, PlayerRef, Optional<T>> getter, Predicate<T> predicate, String error) {
        super(getter, (state, t) -> predicate.test(t), error);
    }
}
