package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.player.TimedPlayerRuleError;

public class PlayerRuleErrorEncoder {
    public static JSONObject encode(PlayerRuleError error) {
        JSONObject jsonError = new JSONObject();
        jsonError.put("category", error.getCategory().toString());
        jsonError.put("message", error.getMessage());

        if(error instanceof TimedPlayerRuleError timedError) {
            long errorExpiration = timedError.getErrorExpirationTime();
            jsonError.put("expiration", errorExpiration);
        }

        return jsonError;
    }
}
