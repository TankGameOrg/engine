package pro.trevor.tankgame.rule.impl.version5;

import pro.trevor.tankgame.rule.impl.util.AbstractAttributeDecoder;

public class AttributeDecoder extends AbstractAttributeDecoder<TankAttribute> {
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
            default -> throw new Error(String.format("Unhandled attribute %s", attribute));
        }
    }
}
