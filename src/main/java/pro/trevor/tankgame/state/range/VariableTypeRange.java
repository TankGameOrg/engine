package pro.trevor.tankgame.state.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.range.DiscreteTypeRange;

import java.util.function.BiFunction;

public interface VariableTypeRange<T, U> extends DiscreteTypeRange<U> {

    void generate(State state, T subject);

}
