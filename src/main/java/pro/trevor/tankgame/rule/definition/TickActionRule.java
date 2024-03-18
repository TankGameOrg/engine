package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.State;

import java.util.function.BiConsumer;

public class TickActionRule<T extends ITickElement> implements IApplicableRule<T> {

    private final BiConsumer<T, State> consumer;

    public TickActionRule(BiConsumer<T, State> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(subject, state);
    }
}
