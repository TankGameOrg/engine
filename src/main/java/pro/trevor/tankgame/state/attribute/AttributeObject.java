package pro.trevor.tankgame.state.attribute;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        JSONObject attributesJsonObject = json.optJSONObject("attributes", new JSONObject());
        for (String key : attributesJsonObject.keySet()) {
            Object attribute = attributesJsonObject.get(key);
            if (attribute instanceof JSONObject jsonAttribute) {
                this.attributes.put(key, Codec.decodeJson(jsonAttribute));
            } else {
                this.attributes.put(key, attributesJsonObject.get(key));
            }
        }
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

        JSONObject attributesJson = new JSONObject();

        for (String attribute : attributes.keySet()) {
            Object value = attributes.get(attribute);
            switch (value) {
                case Boolean v -> attributesJson.put(attribute, v);
                case Integer v -> attributesJson.put(attribute, v);
                case Long v -> attributesJson.put(attribute, v);
                case Float v -> attributesJson.put(attribute, v);
                case Double v -> attributesJson.put(attribute, v);
                case String v -> attributesJson.put(attribute, v);
                case IJsonObject jsonObject -> attributesJson.put(attribute, Codec.encodeJson(jsonObject));
                default ->
                        throw new Error(String.format("Unhandled type %s for attribute %s", value.getClass(), attribute));
            }
        }

        output.put("attributes", attributesJson);
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