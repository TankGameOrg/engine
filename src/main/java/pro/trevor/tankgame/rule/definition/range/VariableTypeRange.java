package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.state.State;

public interface VariableTypeRange<T, U> extends DiscreteTypeRange<U> {

    void generate(State state, T subject);

}
