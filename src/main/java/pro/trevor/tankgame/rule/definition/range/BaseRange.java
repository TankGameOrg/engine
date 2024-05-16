package pro.trevor.tankgame.rule.definition.range;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;

public abstract class BaseRange<T> implements TypeRange<T> {

    protected final String name;
    protected final Class<T> type;

    public BaseRange(String name) {
        this.name = name;
        this.type = getType();
    }

    // Magic to get the parametrized type at runtime
    protected Class<T> getType() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        assert parameterizedType.getActualTypeArguments().length == 1;
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "range");
        output.put("data_type", getDataType());
        output.put("name", getName());
        output.put("range_type", "unbounded");
        return output;
    }

    @Override
    public Class<T> getBoundClass() {
        return type;
    }
}
