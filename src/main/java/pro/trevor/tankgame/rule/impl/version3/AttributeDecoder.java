package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.impl.util.AbstractAttributeDecoder;

public class AttributeDecoder extends AbstractAttributeDecoder<TankAttribute> {
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
