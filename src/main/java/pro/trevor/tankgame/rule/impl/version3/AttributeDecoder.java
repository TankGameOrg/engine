package pro.trevor.tankgame.rule.impl.version3;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.unit.tank.IAttributeDecoder;

import java.util.HashMap;
import java.util.Map;

public class AttributeDecoder implements IAttributeDecoder<TankAttribute> {
    @Override
    public Map<TankAttribute, Object> fromJson(JSONObject json) {
        Map<TankAttribute, Object> output = new HashMap<>();
        for (String key : json.keySet()) {
            TankAttribute attribute = fromString(key);
            if (attribute != null) {
                output.put(fromString(key), json.get(key));
            }
        }
        return output;
    }

    @Override
    public TankAttribute fromString(String attribute) {
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
