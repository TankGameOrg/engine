package pro.trevor.tankgame.state.attribute;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;


/**
 * A Map that can be encoded to and decoded from JSON
 *
 * @param <K> the key type.
 * @param <V> the value type.
 */
@JsonType(name = "AttributeMap")
public class AttributeMap<K, V> implements Map<K, V>, IJsonObject {

    private final Map<K, V> internalMap;

    public AttributeMap() {
        this.internalMap = new HashMap<>();
    }

    public AttributeMap(JSONObject json) {
        this.internalMap = new HashMap<>();
        JSONArray keys = json.optJSONArray("keys");
        JSONArray values = json.optJSONArray("values");
        assert keys.length() == values.length();
        for (int i = 0; i < keys.length(); ++i) {
            Object keyJson = keys.get(i);
            Object valueJson = values.get(i);
            K key;
            V value;

            if (keyJson instanceof JSONObject keyJsonObject) {
                key = (K) Codec.decodeJson(keyJsonObject);
            } else {
                // Assume we are working with a primitive or String
                key = (K) keyJson;
            }

            if (valueJson instanceof JSONObject valueJsonObject) {
                value = (V) Codec.decodeJson(valueJsonObject);
            } else {
                // Assume we are working with a primitive or String
                value = (V) valueJson;
            }

            internalMap.put(key, value);
        }
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return internalMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return internalMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        internalMap.putAll(m);
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray keys = new JSONArray();
        JSONArray values = new JSONArray();
        for (K key : keySet()) {
            keys.put(key);
            values.put(get(key));
        }
        json.put("keys", keys);
        json.put("values", values);
        return json;
    }
}
