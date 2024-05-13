package pro.trevor.tankgame.state.attribute;

public class ActionPointsAttribute extends BaseAttribute<Integer> {

    public static String NAME = "ACTION_POINTS";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Integer.class.getName();
    }

}
