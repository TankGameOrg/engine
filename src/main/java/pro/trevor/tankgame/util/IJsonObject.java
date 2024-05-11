package pro.trevor.tankgame.util;

import org.json.JSONObject;

public interface IJsonObject {

    JSONObject toJson();

    default JSONObject toShortJson() {
        return toJson();
    }
}
