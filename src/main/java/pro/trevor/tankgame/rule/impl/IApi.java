package pro.trevor.tankgame.rule.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.State;

public interface IApi {

    int getVersion();

    State getState();

    void ingestState(JSONObject json);

    void ingestAction(JSONObject json);

    JSONObject getStateJson();

    JSONArray getPossibleActionsJson();
    
}
