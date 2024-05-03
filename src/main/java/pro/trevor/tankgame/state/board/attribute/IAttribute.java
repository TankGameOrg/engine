package pro.trevor.tankgame.state.board.attribute;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public interface IAttribute {
    Class<?> getType();

    static <E extends Enum<E> & IAttribute> E fromString(Class<E> type, String string) {
        try {
            return Enum.valueOf(type, string);
        } catch (Exception ignored) {
        }
        return null;
    }

    static <E extends Enum<E> & IAttribute> Map<E, Object> fromJson(Class<E> type, JSONObject json) {
        Map<E, Object> output = new HashMap<>();
        for (String key : json.keySet()) {
            E attribute = IAttribute.fromString(type, key);
            if (attribute != null) {
                output.put(attribute, json.get(key));
            }
        }
        return output;
    }
}
