package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;

import org.json.JSONObject;

public class DieRollResult<T> {
    private List<T> results;

    public DieRollResult(List<T> results) {
        this.results = results;
    }

    public DieRollResult(JSONObject json) {
        this((List<T>) json.getJSONArray("roll").toList());
    }

    public List<T> getResults() {
        return results;
    }
}
