package rule.impl;

import rule.type.ITickElement;
import state.State;

import java.util.function.BiConsumer;

public class TickAction<T extends ITickElement> implements IApplicableRule<T> {

    private final BiConsumer<T, State> consumer;

    public TickAction(BiConsumer<T, State> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(subject, state);
    }
}
