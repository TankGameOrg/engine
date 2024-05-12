package pro.trevor.tankgame.state.board;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GenericElement implements IPositioned {

    protected Position position;
    protected final Map<String, Object> attributes;

    public GenericElement(Position position, Map<String, Object> defaults) {
        this.position = position;
        this.attributes = new HashMap<>();
        for (String attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    public GenericElement(JSONObject json) {
        this.position = new Position(json.optString("position"));
        this.attributes = new HashMap<>();
        // this.attributes = IAttribute.fromJson(json.getJSONObject("attributes"));
    }

    public Object get(String attribute) {
        return this.attributes.get(attribute);
    }

    public void set(String attribute, Object object) {
        this.attributes.put(attribute, object);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public char toBoardCharacter() {
        return 'U';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();

        output.put("type", "unit");
        output.put("position", position.toBoardString());

        JSONObject attributesJson = new JSONObject();

        for (String attribute : attributes.keySet()) {
            Object value = attributes.get(attribute);
            switch (value) {
                case Boolean v -> attributesJson.put(attribute, v);
                case Integer v -> attributesJson.put(attribute, v);
                case Double v -> attributesJson.put(attribute, v);
                default -> throw new Error(String.format("Unhandled type %s for attribute %s", value.getClass(), attribute));
            }
        }

        output.put("attributes", attributesJson);
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(position.toString());
        for (String key : attributes.keySet()) {
            sb.append(", ");
            sb.append(attributes.get(key).toString());
        }
        sb.append(']');
        return sb.toString();
    }
}
