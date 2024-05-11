package pro.trevor.tankgame.rule.definition;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import pro.trevor.tankgame.state.State;

public class ConditionalRule<T> implements IConditionalRule<T> {

    private final BiPredicate<State, T> predicate;
    private final BiConsumer<State, T> consumer;

    public ConditionalRule(BiPredicate<State, T> predicate, BiConsumer<State, T> consumer) {
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        if (canApply(state, subject)) {
            consumer.accept(state, subject);
        }
    }

    @Override
    public boolean canApply(State state, T subject) {
        return predicate.test(state, subject);
    }
}
