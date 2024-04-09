package pro.trevor.tankgame.util.range;

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
        output.put("type", getBoundClass().getSimpleName().toLowerCase());
        output.put("name", getName());
        return output;
    }
}
