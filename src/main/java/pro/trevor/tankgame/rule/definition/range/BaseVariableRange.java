package pro.trevor.tankgame.rule.definition.range;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseVariableRange<S, T> extends BaseDiscreteRange<T> implements VariableTypeRange<S, T> {

    protected Set<T> elements;

    public BaseVariableRange(String name) {
        super(name, new HashSet<>());
        this.elements = new HashSet<>();
    }

    @Override
    public Set<T> getElements() {
        return elements;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("range_type", "variable");
        return output;
    }
}
