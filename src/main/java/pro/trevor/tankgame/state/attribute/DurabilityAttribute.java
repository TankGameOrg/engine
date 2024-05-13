package pro.trevor.tankgame.state.attribute;

public class DurabilityAttribute extends BaseAttribute<Integer> {

    public static String NAME = "DURABILITY";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Integer.class.getName();
    }

}
