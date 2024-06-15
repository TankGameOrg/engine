package pro.trevor.tankgame.rule.definition.range;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;

import java.util.Set;

public abstract class BaseDiscreteRange<T> extends BaseRange<T> implements DiscreteTypeRange<T> {

    protected final Set<T> elements;

    public BaseDiscreteRange(String name, Set<T> elements) {
        super(name);
        this.elements = elements;
    }

    @Override
    public Set<T> getElements() {
        return elements;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("range_type", "discrete");
        JSONArray range = new JSONArray();
        for (T element : getElements()) {
            if (element instanceof IJsonObject jsonElement) {
                range.put(jsonElement.toJson());
            } else {
                range.put(element);
            }
        }
        output.put("range", range);
        return output;
    }
}
