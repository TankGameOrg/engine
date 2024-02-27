package rule.impl;

import rule.type.IPlayerElement;
import state.State;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class PlayerAction<T extends IPlayerElement> extends ConditionalRule<T> {

    public PlayerAction(BiPredicate<T, State> predicate, BiConsumer<T, State> consumer) {
        super(predicate, consumer);
    }
}
