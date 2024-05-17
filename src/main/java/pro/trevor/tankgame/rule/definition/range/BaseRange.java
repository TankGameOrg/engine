package pro.trevor.tankgame.rule.definition.range;

import org.json.JSONObject;

public abstract class BaseRange<T> implements TypeRange<T> {

    protected final String name;

    public BaseRange(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "range");
        output.put("data_type", getJsonDataType());
        output.put("name", getName());
        output.put("range_type", "unbounded");
        return output;
    }
}
