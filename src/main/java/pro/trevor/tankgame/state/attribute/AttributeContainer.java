package pro.trevor.tankgame.state.attribute;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A type that stores its attributes in a map. These attributes are accessible through Attribute objects.
 * All subclasses of AttributeContainer must implement a constructor <code>Subclass(JSONObject)</code>.
 * The character `$` is used to prefix each attribute key in JSON. When extending this class, refrain from adding JSON
 * keys that begin with a `$`.
 */
@JsonType(name = "AttributeContainer")
public class AttributeContainer {

    protected final Map<String, Object> attributes;

    public AttributeContainer() {
        this.attributes = new HashMap<>();
    }

    public AttributeContainer(Map<Attribute<?>, ?> defaults) {
        this();
        for (Attribute<?> attribute : defaults.keySet()) {
            attributes.put(attribute.getName(), defaults.get(attribute));
        }
    }

    public AttributeContainer(JSONObject json) {
        this.attributes = new HashMap<>();
        for (String jsonKey : json.keySet()) {
            if(isAttributeJsonKey(jsonKey)) {
                Object attribute = json.get(jsonKey);
                if (attribute instanceof JSONObject jsonAttribute) {
                    attribute = Codec.decodeJson(jsonAttribute);
                }
                this.attributes.put(toAttributeString(jsonKey), attribute);
            }
        }
    }

    String toAttributeJsonKeyString(String attribute) {
        return "$" + attribute;
    }

    String toAttributeString(String attributeKey) {
        return attributeKey.substring(1);
    }

    boolean isAttributeJsonKey(String attribute) {
        return attribute.startsWith("$");
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
            if (value instanceof AttributeContainer attributeValue) {
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
        if (!(object instanceof AttributeContainer other)) return false;
        for (String key : attributes.keySet()) {
            if (!attributes.get(key).equals(other.attributes.get(key))) return false;
        }
        return true;
    }

    public boolean has(Attribute<?> attribute) {
        return attributes.containsKey(attribute.getName());
    }

    public <E> Optional<E> get(Attribute<E> attribute) {
        return Optional.ofNullable(getObject(attribute));
    }

    public <E> E getOrElse(Attribute<E> attribute, E defaultValue) {
        if (has(attribute)) {
            return getObject(attribute);
        } else {
            return defaultValue;
        }
    }

    public <E> E getUnsafe(Attribute<E> attribute) {
        if (!has(attribute))
            throw new Error("Attempting to get attribute '" + attribute.getName() + "' from attribute container " + this
                    + ". This generic element has no such attribute");
        return getObject(attribute);
    }

    public <E> void put(Attribute<E> attribute, E value) {
        attributes.put(attribute.getName(), value);
    }

    public <E> void putIfNotPresent(Attribute<E> attribute, E value) {
        if (!has(attribute)) {
            attributes.put(attribute.getName(), value);
        }
    }

    public <E> E remove(Attribute<E> attribute) {
        return attribute.getAttributeClass().cast(attributes.remove(attribute.getName()));
    }

    private enum WrapperClass {
        Long,
        Integer,
        Short,
        Byte,
        Double,
        Float
    }

    private <E> E numberToE(Attribute<E> attribute, Number number) {
        Class<E> attributeClass = attribute.getAttributeClass();

        try {
            WrapperClass wrapper = WrapperClass.valueOf(attributeClass.getSimpleName());
            return attributeClass.cast(switch (wrapper) {
                case Long -> number.longValue();
                case Integer -> number.intValue();
                case Short -> number.shortValue();
                case Byte -> number.byteValue();
                case Double -> number.doubleValue();
                case Float -> number.floatValue();
            });
        } catch (IllegalArgumentException e) {
            throw new Error("Number class " + attributeClass.getSimpleName() + " is not a primitive Number", e);
        }
    }

    private <E> E getObject(Attribute<E> attribute) {
        Object value = attributes.get(attribute.getName());
        try {
            return attribute.getAttributeClass().cast(value);
        } catch (ClassCastException exception) {
            if (value instanceof Number number && Number.class.isAssignableFrom(attribute.getAttributeClass())) {
                return numberToE(attribute, number);
            }
            throw new Error("Error attempting to get attribute '" + attribute.getName() + "' from attribute container " + this
                    + ". Object " + value + " cannot be casted to it's type.", exception);
        }
    }
}
