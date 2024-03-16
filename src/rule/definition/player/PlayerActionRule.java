package rule.definition.player;

import rule.type.IPlayerElement;
import state.State;
import util.ITriConsumer;
import util.ITriPredicate;

public class PlayerActionRule<T extends IPlayerElement, U> implements IPlayerRule<T, U> {

    private final String name;
    private final ITriPredicate<T, U, State> predicate;
    private final ITriConsumer<T, U, State> consumer;


    public PlayerActionRule(String name, ITriPredicate<T, U, State> predicate, ITriConsumer<T, U, State> consumer) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject, U target) {
        if (canApply(state, subject, target)) {
            consumer.accept(subject, target, state);
        }
    }

    @Override
    public boolean canApply(State state, T subject, U target) {
        return predicate.test(subject, target, state);
    }

    @Override
    public String name() {
        return name;
    }
}
