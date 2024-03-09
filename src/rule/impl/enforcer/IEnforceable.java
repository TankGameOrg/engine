package rule.impl.enforcer;

import state.State;

public interface IEnforceable<T> {
    void enforce(State state, T subject);
}
