package pro.trevor.tankgame.state.range;

import org.json.JSONObject;
import pro.trevor.tankgame.util.range.BaseDiscreteRange;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseVariableRange<S, T> extends BaseDiscreteRange<T> implements VariableTypeRange<S, T> {

    protected Set<T> elements;

    public BaseVariableRange(String name) {
        super(name, new HashSet<>());
        this.elements = new HashSet<>();
    }

    // Magic to get the parametrized type at runtime
    @Override
    protected Class<T> getType() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        assert parameterizedType.getActualTypeArguments().length == 2;
        return (Class<T>) parameterizedType.getActualTypeArguments()[1];
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
