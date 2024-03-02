package rule.impl.player;

import state.State;

public interface IPlayerRule<T, U> {

    void apply(State state, T subject, U target);
    boolean canApply(State state, T subject, U target);

}
