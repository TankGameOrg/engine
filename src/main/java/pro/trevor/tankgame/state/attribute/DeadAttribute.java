package pro.trevor.tankgame.state.attribute;

public class DeadAttribute extends BaseAttribute<Boolean> {

    public static String NAME = "DEAD";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getType() {
        return Boolean.class.getName();
    }

}
