package pro.trevor.tankgame.rule.definition;

import org.json.JSONObject;

public class EngineError extends Error {

    private final JSONObject json;

    public EngineError(JSONObject json) {
        this.json = json;
    }

    public JSONObject getJson() {
        return json;
    }
}
