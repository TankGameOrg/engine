package pro.trevor.tankgame.rule.impl.version5;

import pro.trevor.tankgame.state.board.unit.tank.IAttribute;

public enum TankAttribute implements IAttribute {
    BOUNTY(Integer.class),
    DEAD(Boolean.class),
    DURABILITY(Integer.class),
    MAX_DURABILITY(Integer.class),
    ACTIONS(Integer.class),
    ACTIONS_PER_DAY(Integer.class),
    MAX_ACTIONS(Integer.class),
    RANGE(Integer.class),
    GOLD(Integer.class),
    MAX_GOLD(Integer.class),
    MOVE_SPEED(Integer.class),
    HIT_CHANCE(Double.class),
    ATTACK_DAMAGE(Integer.class);

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
        public static final String MAX_DURABILITY = "MAX_DURABILITY";
        public static final String ACTIONS = "ACTIONS";
        public static final String ACTIONS_PER_DAY = "ACTIONS_PER_DAY";
        public static final String MAX_ACTIONS = "MAX_ACTIONS";
        public static final String RANGE = "RANGE";
        public static final String GOLD = "GOLD";
        public static final String MAX_GOLD = "MAX_GOLD";
        public static final String MOVE_SPEED = "MOVE_SPEED";
        public static final String HIT_CHANCE = "HIT_CHANCE";
        public static final String ATTACK_DAMAGE = "ATTACK_DAMAGE";
    }

}