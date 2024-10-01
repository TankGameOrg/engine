package pro.trevor.tankgame.ui.rpc;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;

public class Settings implements IJsonObject {

    private static final String ALLOW_MANUAL_ROLLS_KEY = "allowManualRolls";

    public boolean allowManualRolls;

    public Settings(JSONObject json) {
        allowManualRolls = json.getBoolean(ALLOW_MANUAL_ROLLS_KEY);
    }

    public Settings() {
        allowManualRolls = true;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();

        output.put(ALLOW_MANUAL_ROLLS_KEY, allowManualRolls);

        return output;
    }
}
