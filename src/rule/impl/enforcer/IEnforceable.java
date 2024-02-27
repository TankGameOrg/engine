package rule.impl.enforcer;

import state.State;

public interface IEnforceable<T, U extends Comparable<U>> {
    void enforce(State state, T subject);
}
