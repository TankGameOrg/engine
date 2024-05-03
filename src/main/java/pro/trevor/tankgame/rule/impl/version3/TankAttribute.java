package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.state.board.attribute.AbstractAttributeDecoder;
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

    public static final AttributeDecoder DECODER = new AttributeDecoder();
    public static class AttributeDecoder extends AbstractAttributeDecoder<TankAttribute> {
        protected AttributeDecoder() {}

        @Override
        public TankAttribute fromSource(String attribute) {
            switch (attribute.toUpperCase()) {
                case TankAttribute.Name.ACTIONS -> {
                    return TankAttribute.ACTIONS;
                }
                case TankAttribute.Name.BOUNTY -> {
                    return TankAttribute.BOUNTY;
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
                case TankAttribute.Name.RANGE -> {
                    return TankAttribute.RANGE;
                }
            }
            return null;
        }
    }
}