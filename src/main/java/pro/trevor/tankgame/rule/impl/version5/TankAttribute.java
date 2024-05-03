package pro.trevor.tankgame.rule.impl.version5;

import pro.trevor.tankgame.state.board.attribute.AbstractAttributeDecoder;
import pro.trevor.tankgame.state.board.attribute.DurableAttribute;
import pro.trevor.tankgame.state.board.attribute.IAttribute;
import pro.trevor.tankgame.state.board.attribute.IAttributeDecoder;

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

    public static final AttributeDecoder DECODER = new AttributeDecoder();
    public static class AttributeDecoder extends AbstractAttributeDecoder<TankAttribute> {
        protected AttributeDecoder() {}

        @Override
        public TankAttribute fromSource(String attribute) {
            switch (attribute) {
                case TankAttribute.Name.BOUNTY -> {
                    return TankAttribute.BOUNTY;
                }
                case TankAttribute.Name.ACTIONS -> {
                    return TankAttribute.ACTIONS;
                }
                case TankAttribute.Name.ATTACK_DAMAGE -> {
                    return TankAttribute.ATTACK_DAMAGE;
                }
                case TankAttribute.Name.ACTIONS_PER_DAY -> {
                    return TankAttribute.ACTIONS_PER_DAY;
                }
                case TankAttribute.Name.DEAD -> {
                    return TankAttribute.DEAD;
                }
                case TankAttribute.Name.DURABILITY -> {
                    return TankAttribute.DURABILITY;
                }
                case TankAttribute.Name.GOLD -> {
                    return TankAttribute.GOLD;
                }
                case TankAttribute.Name.HIT_CHANCE -> {
                    return TankAttribute.HIT_CHANCE;
                }
                case TankAttribute.Name.MAX_ACTIONS -> {
                    return TankAttribute.MAX_ACTIONS;
                }
                case TankAttribute.Name.MAX_DURABILITY -> {
                    return TankAttribute.MAX_DURABILITY;
                }
                case TankAttribute.Name.MAX_GOLD -> {
                    return TankAttribute.MAX_GOLD;
                }
                case TankAttribute.Name.MOVE_SPEED -> {
                    return TankAttribute.MOVE_SPEED;
                }
                case TankAttribute.Name.RANGE -> {
                    return TankAttribute.RANGE;
                }
                default -> {
                    return null;
                }
            }
        }
    }

}