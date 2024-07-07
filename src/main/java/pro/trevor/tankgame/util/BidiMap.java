package pro.trevor.tankgame.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidiMap<K, V> {
    private final Map<K, V> keyToValue;
    private final Map<V, K> valueToKey;

    public BidiMap() {
        keyToValue = new HashMap<>();
        valueToKey = new HashMap<>();
    }

    public BidiMap(List<Pair<K, V>> pairs) {
        keyToValue = new HashMap<>();
        valueToKey = new HashMap<>();
        for(Pair<K, V> pair : pairs) {
            put(pair.left(), pair.right());
        }
    }

    public void put(K key, V value) {
        assert !keyToValue.containsKey(key);
        assert !valueToKey.containsKey(value);
        keyToValue.put(key, value);
        valueToKey.put(value, key);
    }

    public V getValue(K key) {
        return keyToValue.get(key);
    }

    public K getKey(V value) {
        return valueToKey.get(value);
    }
}
