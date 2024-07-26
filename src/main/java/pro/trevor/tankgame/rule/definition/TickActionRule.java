package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.State;

import java.util.function.BiConsumer;

public class TickActionRule<T extends ITickElement> implements IApplicableRule<T> {

    private final BiConsumer<State, T> consumer;
    private final Priority priority;

    public TickActionRule(BiConsumer<State, T> consumer) {
        this.consumer = consumer;
        this.priority = Priority.DEFAULT;
    }

    public TickActionRule(BiConsumer<State, T> consumer, Priority priority) {
        this.consumer = consumer;
        this.priority = priority;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(state, subject);
    }
}
