package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class GenericElement implements IElement {

    protected final Map<String, Object> attributes;

    public GenericElement(Map<String, Object> defaults) {
        this.attributes = new HashMap<>();
        for (String attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    public GenericElement(JSONObject json) {
        this.attributes = new HashMap<>();
        JSONObject attributesJsonObject = json.getJSONObject("attributes");
        for (String key : attributesJsonObject.keySet()) {
            Object attribute = attributesJsonObject.get(key);
            if (attribute instanceof JSONObject jsonAttribute) {
                String qualifiedClass = jsonAttribute.getString("class");
                try {
                    Class<?> attributeClass = Class.forName(qualifiedClass);
                    attributeClass.getConstructor(JSONObject.class).newInstance(jsonAttribute);
                } catch (ClassNotFoundException e) {
                    throw new Error("Error reading attribute " + key + ": unknown class " + qualifiedClass, e);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException e) {
                    throw new Error("No matching constructor found for class " + qualifiedClass, e);
                }
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

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();

        JSONObject attributesJson = new JSONObject();

        for (String attribute : attributes.keySet()) {
            Object value = attributes.get(attribute);
            switch (value) {
                case Boolean v -> attributesJson.put(attribute, v);
                case Integer v -> attributesJson.put(attribute, v);
                case Double v -> attributesJson.put(attribute, v);
                case IJsonObject json -> attributesJson.put(attribute, json.toJson());
                default ->
                    throw new Error(String.format("Unhandled type %s for attribute %s", value.getClass(), attribute));
            }
        }

        output.put("attributes", attributesJson);
        return output;
    }
}
