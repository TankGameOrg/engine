package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.board.Position;

import java.util.Map;

/**
 * A generic cooldown tank that has one cooldown shared across all actions.
 * Actions still each define their cooldowns
 * individually.
 */
public class GlobalCooldownTank extends GenericTank implements ICooldownPlayerElement {

    protected long lastActionTime;

    public GlobalCooldownTank(String player, Position position, Map<String, Object> defaults) {
        super(player, position, defaults);
        this.lastActionTime = 0;
    }

    public GlobalCooldownTank(JSONObject json) {
        super(json);
        this.lastActionTime = json.optLong("last_action_time", 0);
    }

    @Override
    public long getLastUsage(String rule) {
        return lastActionTime;
    }

    @Override
    public void setLastUsage(String rule, long time) {
        this.lastActionTime = time;
    }

    @Override
    public JSONObject toJson() {
        return super.toJson().put("last_action_time", lastActionTime);
    }
}
