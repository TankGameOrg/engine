package pro.trevor.tankgame.rule.impl.version5;

import pro.trevor.tankgame.state.board.attribute.IAttribute;

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
}
