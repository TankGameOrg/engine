package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.State;

import java.util.function.BiConsumer;

public class TickActionRule<T extends ITickElement> implements IApplicableRule<T> {

    private final BiConsumer<State, T> consumer;

    public TickActionRule(BiConsumer<State, T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(state, subject);
    }
}
