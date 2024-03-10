package rule.definition;

import state.State;

public interface IApplicableRule<T> {
    void apply(State state, T subject);

}
