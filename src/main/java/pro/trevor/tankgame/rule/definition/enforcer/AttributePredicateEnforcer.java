package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

import java.util.function.Function;
import java.util.function.Predicate;

public class AttributePredicateEnforcer<T extends AttributeContainer, U extends Comparable<U>> implements IEnforceable<T> {

    private final Predicate<T> predicate;
    private final Attribute<U> attribute;
    private final Function<T, U> boundFunction;

    public AttributePredicateEnforcer(Predicate<T> predicate, Attribute<U> attribute, Function<T, U> boundFunction) {
        this.predicate = predicate;
        this.attribute = attribute;
        this.boundFunction = boundFunction;
    }

    @Override
    public void enforce(State state, T subject) {
        if (!predicate.test(subject)) {
            attribute.to(subject, boundFunction.apply(subject));
        }
    }
}

