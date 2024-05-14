package pro.trevor.tankgame.state.range;

import pro.trevor.tankgame.state.State;

import java.util.Set;
import java.util.function.BiFunction;

public abstract class FunctionVariableRange<S, T> extends BaseVariableRange<S, T> {

    protected final BiFunction<State, S, Set<T>> generator;

    public FunctionVariableRange(String name, BiFunction<State, S, Set<T>> generator) {
        super(name);
        this.generator = generator;
    }

    @Override
    public void generate(State state, S subject) {
        if (state == null) {
            elements.clear();
        } else {
            elements = generator.apply(state, subject);
        }
    }
}
