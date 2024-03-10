package rule.definition.enforcer;

import state.State;

public interface IEnforceable<T> {
    void enforce(State state, T subject);
}
