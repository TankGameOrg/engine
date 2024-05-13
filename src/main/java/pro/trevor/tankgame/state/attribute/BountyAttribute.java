package pro.trevor.tankgame.state.attribute;

public class BountyAttribute extends BaseAttribute<Integer> {

    public static String NAME = "BOUNTY";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Integer.class.getName();
    }

}
