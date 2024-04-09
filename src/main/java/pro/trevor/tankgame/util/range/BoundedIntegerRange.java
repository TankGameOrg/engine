package pro.trevor.tankgame.util.range;

public class BoundedIntegerRange extends BaseBoundedRange<Integer> {

    public BoundedIntegerRange(String name, int lower, int upper) {
        super(name, lower, upper);
    }

    @Override
    public Class<Integer> getBoundClass() {
        return Integer.class;
    }
}
