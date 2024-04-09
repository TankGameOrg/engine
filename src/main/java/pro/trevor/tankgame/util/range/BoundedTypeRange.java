package pro.trevor.tankgame.util.range;

public interface BoundedTypeRange<T> extends TypeRange<T> {

    T lowerBound();
    T upperBound();

}
