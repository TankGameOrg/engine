package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.function.BiFunction;

public class BooleanPredicate<T extends AttributeContainer & IPlayerElement> extends AttributePredicate<T> {

    public BooleanPredicate(BiFunction<State, PlayerRef, T> getter, Attribute<Boolean> attribute, boolean expected, String error) {
        super(getter, (t) -> attribute.in(t) && attribute.unsafeFrom(t).equals(expected), error);
    }
}
