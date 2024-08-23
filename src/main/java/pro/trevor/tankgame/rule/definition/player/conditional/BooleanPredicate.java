package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Optional;
import java.util.function.BiFunction;

public class BooleanPredicate<T extends AttributeContainer> extends AttributePredicate<T> {
    public BooleanPredicate(BiFunction<State, PlayerRef, Optional<T>> getter, Attribute<Boolean> attribute, boolean expected, String error) {
        super(getter, (t) -> t.has(attribute) && t.getUnsafe(attribute).equals(expected), error);
    }
}
