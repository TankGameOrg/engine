package pro.trevor.tankgame.state.board.unit;

import java.util.Map;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.attribute.IAttribute;

/**
 * A generic cooldown tank that has one cooldown shared across all actions. Actions still each
 * define their cooldowns individually.
 *
 * @param <E> The attribute enum to be used for storing attributes about the tank.
 */
public class GlobalCooldownTank<E extends Enum<E> & IAttribute> extends GenericTank<E>
        implements ICooldownPlayerElement {

    protected long lastActionTime;

    public GlobalCooldownTank(String player, Position position, Map<E, Object> defaults) {
        super(player, position, defaults);
        this.lastActionTime = 0;
    }

    public GlobalCooldownTank(JSONObject json, Class<E> type) {
        super(json, type);
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
