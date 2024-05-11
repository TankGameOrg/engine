package pro.trevor.tankgame.state.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.range.DiscreteTypeRange;

public interface VariableTypeRange<T, U> extends DiscreteTypeRange<U> {

    void generate(State state, T subject);
}
