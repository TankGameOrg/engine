package pro.trevor.tankgame.rule.impl;

import org.json.JSONObject;

public interface IApi {

    int getVersion();

    void ingestState(JSONObject json);

    void ingestAction(JSONObject json);

    void printStateJson(boolean humanReadable);

    void printPossibleMovesJson(boolean humanReadable);
    
}
