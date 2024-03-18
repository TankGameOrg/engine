package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.State;

public interface IEnforceable<T> {
    void enforce(State state, T subject);
}
