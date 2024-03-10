package rule.definition;

import state.State;

public interface IConditionalRule<T> extends IApplicableRule<T> {

    boolean canApply(State state, T subject);

}
