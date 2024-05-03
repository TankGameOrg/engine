package pro.trevor.tankgame.state.board.attribute;

public enum DurableAttribute implements IAttribute {
    DURABILITY;

    @Override
    public Class<?> getType() {
        return Integer.class;
    }
}
