package pro.trevor.tankgame.util.range;

public class IntegerRange extends BaseRange<Integer> {

    public IntegerRange(String name) {
        super(name);
    }

    @Override
    public Class<Integer> getBoundClass() {
        return Integer.class;
    }
}
