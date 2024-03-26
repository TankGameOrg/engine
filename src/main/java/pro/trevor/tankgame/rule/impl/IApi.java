package pro.trevor.tankgame.rule.impl;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IApi {

    int getVersion();

    void ingestState(JSONObject json);

    void ingestAction(JSONObject json);

    JSONObject getStateJson();

    JSONArray getPossibleActionsJson();
    
}
