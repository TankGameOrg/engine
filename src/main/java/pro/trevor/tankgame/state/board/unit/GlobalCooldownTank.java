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

    protected long cooldownEndTime;

    public GlobalCooldownTank(String player, Position position, Map<String, Object> defaults) {
        super(player, position, defaults);
        this.cooldownEndTime = 0;
    }

    public GlobalCooldownTank(JSONObject json) {
        super(json);
        this.cooldownEndTime = json.optLong("global_cooldown_end_time", 0);
    }

    @Override
    public long getCooldownEnd(String rule) {
        return cooldownEndTime;
    }

    @Override
    public void setCooldownEnd(String rule, long time) {
        this.cooldownEndTime = time;
    }

    @Override
    public JSONObject toJson() {
        return super.toJson().put("global_cooldown_end_time", cooldownEndTime);
    }
}
