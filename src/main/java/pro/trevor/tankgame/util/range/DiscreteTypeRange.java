package pro.trevor.tankgame.util.range;

import java.util.Set;

public interface DiscreteTypeRange<T> extends TypeRange<T> {

    Set<T> getElements();
}
