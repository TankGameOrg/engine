package pro.trevor.tankgame.rule.impl;

import org.json.JSONObject;
import pro.trevor.tankgame.state.State;

// All instances of IApi are expected to have a constructor which accepts no arguments
public interface IApi {

    State getState();

    JSONObject getRules();

    void ingestState(JSONObject json);

    void ingestAction(JSONObject json);

    JSONObject getStateJson();

    JSONObject getPossibleActions(String player);
    
}
