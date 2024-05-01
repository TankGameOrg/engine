package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.state.board.unit.tank.IAttribute;

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

    /**
     * Static comptime references to the names of each enumeration.
     * These must match EXACTLY with the output of enumeration::name.
     */
    public static final class Name {
        public static final String BOUNTY = "BOUNTY";
        public static final String DEAD = "DEAD";
        public static final String DURABILITY = "DURABILITY";
        public static final String ACTIONS = "ACTIONS";
        public static final String RANGE = "RANGE";
        public static final String GOLD = "GOLD";
    }

}