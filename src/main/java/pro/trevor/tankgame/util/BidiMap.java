package pro.trevor.tankgame.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidiMap<Key, Value> {
    private Map<Key, Value> keyToValue = new HashMap<>();
    private Map<Value, Key> valueToKey = new HashMap<>();

    public BidiMap(List<Pair<Key, Value>> pairs) {
        for(Pair<Key, Value> pair : pairs) {
            keyToValue.put(pair.left(), pair.right());
            valueToKey.put(pair.right(), pair.left());
        }
    }

    Value getValue(Key key) {
        return keyToValue.get(key);
    }

    Key getKey(Value value) {
        return valueToKey.get(value);
    }
}
