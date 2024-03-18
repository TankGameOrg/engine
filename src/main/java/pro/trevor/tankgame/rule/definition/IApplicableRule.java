package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.state.State;

public interface IApplicableRule<T> {
    void apply(State state, T subject);

}
