package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

import java.util.Map;

/**
 * A generic cooldown tank that has one cooldown shared across all actions.
 * Actions still each define their cooldowns individually.
 */
@JsonType(name = "GlobalCooldownTank")
public class GlobalCooldownTank extends GenericTank implements ICooldownPlayerElement {

    public GlobalCooldownTank(String player, Position position, Map<String, Object> defaults) {
        super(player, position, defaults);
        Attribute.TIME_OF_LAST_ACTION.toIfNotPresent(this, 0L);
    }

    public GlobalCooldownTank(JSONObject json) {
        super(json);
        Attribute.TIME_OF_LAST_ACTION.toIfNotPresent(this, 0L);
    }

    @Override
    public long getLastUsage(String rule) {
        return Attribute.TIME_OF_LAST_ACTION.unsafeFrom(this);
    }

    @Override
    public void setLastUsage(String rule, long time) {
        Attribute.TIME_OF_LAST_ACTION.to(this, time);
    }

}
