package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class MinimumEnforcer<T extends AttributeContainer, U extends Comparable<U>> extends AttributePredicateEnforcer<T, U> {

    public MinimumEnforcer(Attribute<U> attribute, U bound) {
        super((x) -> x.has(attribute) && x.getUnsafe(attribute).compareTo(bound) > 0, attribute, (t) -> bound);
    }

    public MinimumEnforcer(Attribute<U> attribute, Attribute<U> boundAttribute, U defaultBound) {
        super(
            (x) -> {
                if(x.has(attribute)) {
                    U bound = x.getOrElse(boundAttribute, defaultBound);
                    return x.getUnsafe(attribute).compareTo(bound) > 0;
                }

                return false;
            },
            attribute,
            (x) -> x.getOrElse(boundAttribute, defaultBound));
    }
}
