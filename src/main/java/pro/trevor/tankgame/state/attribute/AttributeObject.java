package pro.trevor.tankgame.state.attribute;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A type that stores its attributes in a map. These attributes are accessible through Attribute objects.
 * All subclasses of AttributeObject must implement a constructor <code>Subclass(JSONObject)</code>.
 * The character `$` is used to prefix each attribute key in JSON. When extending this class, refrain from adding JSON
 * keys that begin with a `$`.
 */
@JsonType(name = "AttributeObject")
public class AttributeObject {

    protected final Map<String, Object> attributes;

    public AttributeObject() {
        this.attributes = new HashMap<>();
    }

    public AttributeObject(Map<String, Object> defaults) {
        this();
        for (String attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    public AttributeObject(JSONObject json) {
        this.attributes = new HashMap<>();
        for (String jsonKey : json.keySet().stream().filter(AttributeObject::isAttributeJsonKey).toList()) {
            Object attribute = json.get(jsonKey);
            String key = toAttributeString(jsonKey);
            if (attribute instanceof JSONObject jsonAttribute) {
                this.attributes.put(key, Codec.decodeJson(jsonAttribute));
            } else {
                this.attributes.put(key, attribute);
            }
        }
    }

    static String toAttributeJsonKeyString(String attribute) {
        return "$" + attribute;
    }

    static String toAttributeString(String attributeKey) {
        return attributeKey.substring(1);
    }

    static boolean isAttributeJsonKey(String attribute) {
        return attribute.startsWith("$");
    }

    public boolean has(String attribute) {
        return this.attributes.containsKey(attribute);
    }

    public Object get(String attribute) {
        return this.attributes.get(attribute);
    }

    public void set(String attribute, Object object) {
        this.attributes.put(attribute, object);
    }

    public Object remove(String attribute) {
        return this.attributes.remove(attribute);
    }

    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        for (String attribute : attributes.keySet()) {
            Object value = attributes.get(attribute);
            String attributeKey = toAttributeJsonKeyString(attribute);
            switch (value) {
                case Boolean v -> output.put(attributeKey, v);
                case Integer v -> output.put(attributeKey, v);
                case Long v -> output.put(attributeKey, v);
                case Float v -> output.put(attributeKey, v);
                case Double v -> output.put(attributeKey, v);
                case String v -> output.put(attributeKey, v);
                case IJsonObject jsonObject -> output.put(attributeKey, Codec.encodeJson(jsonObject));
                default ->
                        throw new Error(String.format("Unhandled type %s for attribute %s", value.getClass(), attribute));
            }
        }

        output.put("class", Codec.typeFromClass(getClass()));
        return output;
    }

    @Override
    public String toString() {
        return toString(2);
    }

    public String toString(int indent) {
        StringBuilder output = new StringBuilder();
        for (String attributeKey : attributes.keySet()) {
            Object value = attributes.get(attributeKey);
            output.repeat(" ", indent).append(attributeKey).append(":");
            if (value instanceof AttributeObject attributeValue) {
                output.append(System.lineSeparator()).append(attributeValue.toString(indent + 2));
            } else {
                output.append(' ').append(attributes.get(attributeKey)).append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes.keySet(), attributes.values());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AttributeObject other)) return false;
        for (String key : attributes.keySet()) {
            if (!attributes.get(key).equals(other.attributes.get(key))) return false;
        }
        return true;
    }
}