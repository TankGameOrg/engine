package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.state.board.attribute.IAttribute;

public enum TankAttribute implements IAttribute {
    ACTIONS(Integer.class),
    BOUNTY(Integer.class),
    DEAD(Boolean.class),
    DURABILITY(Integer.class),
    GOLD(Integer.class),
    RANGE(Integer.class);

    private final Class<?> type;

    TankAttribute(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
