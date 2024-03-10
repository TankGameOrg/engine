package rule.definition.player;

import rule.type.IPlayerElement;
import state.State;

public class PlayerActionRule<T extends IPlayerElement, U> implements IPlayerRule<T, U> {

    private final ITriPredicate<T, U, State> predicate;
    private final ITriConsumer<T, U, State> consumer;


    public PlayerActionRule(ITriPredicate<T, U, State> predicate, ITriConsumer<T, U, State> consumer) {
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
}
