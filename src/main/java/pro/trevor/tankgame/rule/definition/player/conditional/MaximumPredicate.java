package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.function.BiFunction;

public class MaximumPredicate<A extends Comparable<A>, T extends AttributeContainer & IPlayerElement> extends AttributePredicate<T> {

    public MaximumPredicate(BiFunction<State, PlayerRef, T> getter, Attribute<A> attribute, A bound, String error) {
        super(getter, (t) -> (t.has(attribute) && t.getUnsafe(attribute).compareTo(bound) <= 0), error);
    }
}
