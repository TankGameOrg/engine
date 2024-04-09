package pro.trevor.tankgame.util.range;

public abstract class BaseBoundedRange<T> extends BaseRange<T> implements BoundedTypeRange<T> {

    protected final T lower;
    protected final T upper;

    public BaseBoundedRange(String name, T lower, T upper) {
        super(name);
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public T lowerBound() {
        return lower;
    }

    @Override
    public T upperBound() {
        return upper;
    }
}
