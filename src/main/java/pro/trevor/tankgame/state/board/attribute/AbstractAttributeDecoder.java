package pro.trevor.tankgame.state.board.attribute;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAttributeDecoder<E extends Enum<E> & IAttribute> implements IAttributeDecoder<E> {
    @Override
    public Map<E, Object> fromJsonAttributes(JSONObject json) {
        Map<E, Object> output = new HashMap<>();
        for (String key : json.keySet()) {
            E attribute = fromSource(key);
            if (attribute != null) {
                output.put(fromSource(key), json.get(key));
            }
        }
        return output;
    }
}