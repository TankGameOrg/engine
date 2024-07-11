package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.function.BiFunction;

public class MinimumPredicate<A extends Comparable<A>, T extends AttributeObject & IPlayerElement> extends AttributePredicate<T> {

    public MinimumPredicate(BiFunction<State, PlayerRef, T> getter, Attribute<A> attribute, A bound, String error) {
        super(getter, (t) -> (attribute.in(t) && attribute.unsafeFrom(t).compareTo(bound) >= 0), error);
    }
}
