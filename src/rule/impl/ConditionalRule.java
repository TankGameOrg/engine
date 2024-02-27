package rule.impl;

import state.State;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class ConditionalRule<T> implements IConditionalRule<T> {

    private final BiPredicate<T, State> predicate;
    private final BiConsumer<T, State> consumer;

    public ConditionalRule(BiPredicate<T, State> predicate, BiConsumer<T, State> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        if (canApply(state, subject)) {
            consumer.accept(subject, state);
        }
    }

    @Override
    public boolean canApply(State state, T subject) {
        return predicate.test(subject, state);
    }
}
