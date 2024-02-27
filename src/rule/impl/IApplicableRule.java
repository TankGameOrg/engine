package rule.impl;

import state.State;

public interface IApplicableRule<T> {
    void apply(State state, T element);

}
