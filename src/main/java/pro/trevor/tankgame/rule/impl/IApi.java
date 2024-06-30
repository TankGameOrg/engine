package pro.trevor.tankgame.rule.impl;

import org.json.JSONObject;
import pro.trevor.tankgame.state.State;

// All instances of IApi are expected to have a constructor which accepts no arguments
public interface IApi {

    State getState();

    void setState(State state);

    JSONObject getRules();

    void ingestAction(JSONObject json);

    JSONObject getPossibleActions(String player);
    
}
