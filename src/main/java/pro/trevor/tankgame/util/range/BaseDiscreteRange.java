package pro.trevor.tankgame.util.range;

import java.util.Set;

public abstract class BaseDiscreteRange<T> extends BaseRange<T> implements DiscreteTypeRange<T> {

    protected final Set<T> elements;

    public BaseDiscreteRange(String name, Set<T> elements) {
        super(name);
        this.elements = elements;
    }

    @Override
    public Set<T> getElements() {
        return elements;
    }
}
