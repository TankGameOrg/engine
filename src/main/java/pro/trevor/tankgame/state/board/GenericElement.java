package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.attribute.IAttribute;

import java.util.HashMap;
import java.util.Map;

public class GenericElement<E extends Enum<E> & IAttribute> implements IElement, IPositioned {

    protected Position position;
    protected final Map<E, Object> attributes;

    public GenericElement(Position position, Map<E, Object> defaults) {
        this.position = position;
        this.attributes = new HashMap<>();
        for (E attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    public GenericElement(JSONObject json, Class<E> type) {
        this.position = new Position(json.optString("position"));
        this.attributes = IAttribute.fromJson(type, json.getJSONObject("attributes"));
    }

    protected <T> T get(E attribute, Class<T> type) {
        if (attribute.getType().isAssignableFrom(type)) {
            try {
                return type.cast(attributes.get(attribute));
            } catch (ClassCastException ignored) {
                throw new Error(String.format("Attribute %s was not stored as a(n) %s", attribute.name(), type.getSimpleName()));
            }
        } else {
            throw new Error(String.format("Unable to read attribute %s as a(n) %s", attribute.name(), type.getSimpleName()));
        }
    }

    protected void set(E attribute, Object object) {
        if (attribute.getType().isAssignableFrom(object.getClass())) {
            attributes.put(attribute, object);
        } else {
            throw new Error(String.format("Attribute %s cannot store a(n) %s", attribute.name(), object.getClass().getSimpleName()));
        }
    }

    public int getInteger(E attribute) {
        return get(attribute, Integer.class);
    }

    public double getDouble(E attribute) {
        return get(attribute, Double.class);
    }

    public boolean getBoolean(E attribute) {
        return get(attribute, Boolean.class);
    }

    public void setInteger(E attribute, int value)
    {
        set(attribute, value);
    }

    public void setDouble(E attribute, double value)
    {
        set(attribute, value);
    }

    public void setBoolean(E attribute, boolean value)
    {
        set(attribute, value);
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

        for (E attribute : attributes.keySet()) {
            String attributeName = attribute.name();
            Object value = attributes.get(attribute);
            switch (value) {
                case Boolean v -> attributesJson.put(attributeName, v);
                case Integer v -> attributesJson.put(attributeName, v);
                case Double v -> attributesJson.put(attributeName, v);
                default -> throw new Error(String.format("Unhandled type %s for attribute %s", attribute.getType().getSimpleName(), attributeName));
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
        for (E key : attributes.keySet()) {
            sb.append(", ");
            sb.append(attributes.get(key).toString());
        }
        sb.append(']');
        return sb.toString();
    }
}
