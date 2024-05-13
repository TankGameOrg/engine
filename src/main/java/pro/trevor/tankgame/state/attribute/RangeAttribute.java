package pro.trevor.tankgame.state.attribute;

public class RangeAttribute extends BaseAttribute<Integer> {

    public static String NAME = "RANGE";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Integer.class.getName();
    }

}
