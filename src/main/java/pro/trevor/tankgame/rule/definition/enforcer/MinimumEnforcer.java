package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;

public class MinimumEnforcer<T extends AttributeObject, U extends Comparable<U>> extends AttributePredicateEnforcer<T, U> {

    public MinimumEnforcer(Attribute<U> attribute, U bound) {
        super((x) -> attribute.in(x) && attribute.unsafeFrom(x).compareTo(bound) > 0, attribute, bound);
    }
}
