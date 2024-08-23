package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class MaximumEnforcer <T extends AttributeContainer, U extends Comparable<U>> extends AttributePredicateEnforcer<T, U> {
    public MaximumEnforcer(Attribute<U> attribute, U bound) {
        super((x) -> x.has(attribute) && x.getUnsafe(attribute).compareTo(bound) < 0, attribute, (x) -> bound);
    }

    public MaximumEnforcer(Attribute<U> attribute, Attribute<U> boundAttribute, U defaultBound) {
        super(
            (x) -> {
                if(x.has(attribute)) {
                    U bound = x.getOrElse(boundAttribute, defaultBound);
                    return x.getUnsafe(attribute).compareTo(bound) < 0;
                }

                return false;
            },
            attribute,
            (x) -> x.getOrElse(boundAttribute, defaultBound));
    }
}
