package rule.impl.enforcer;

import state.State;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PredicateEnforcer<T, U extends Comparable<U>> implements IEnforceable<T> {

    private final Predicate<T> predicate;
    private final BiConsumer<T, U> setter;
    private final U bound;

    public PredicateEnforcer(Predicate<T> predicate, BiConsumer<T, U> setter, U bound) {
        this.predicate = predicate;
        this.setter = setter;
        this.bound = bound;
    }

    @Override
    public void enforce(State state, T subject) {
        if (!predicate.test(subject)) {
            setter.accept(subject, bound);
        }
    }
}

