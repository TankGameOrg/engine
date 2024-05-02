package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.IAttribute;
import pro.trevor.tankgame.state.board.unit.tank.IAttributeDecoder;

import java.util.*;

public class GenericUnit<E extends Enum<E> & IAttribute> implements IUnit {

    protected Position position;
    protected final Map<E, Object> attributes;

    public GenericUnit(Position position, Map<E, Object> defaults) {
        this.position = position;
        this.attributes = new HashMap<>();
        for (E attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    public GenericUnit(JSONObject json, IAttributeDecoder<E> attributeDecoder) {
        this.position = Position.fromJson(json.getJSONObject("position"));
        this.attributes = attributeDecoder.fromJsonAttributes(json.getJSONObject("attributes"));
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
        output.put("position", position.toJson());

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
        return null;
    }
}
