package pro.trevor.tankgame.state.attribute;

public class GoldAttribute extends BaseAttribute<Integer> {

    public static String NAME = "GOLD";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Integer.class.getName();
    }

}