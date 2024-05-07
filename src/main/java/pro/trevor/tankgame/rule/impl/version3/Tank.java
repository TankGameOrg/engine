package pro.trevor.tankgame.rule.impl.version3;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.board.unit.GenericTank;

public class Tank extends GenericTank<TankAttribute> implements ICooldownPlayerElement {

    private long lastUseMs;

    public Tank(JSONObject json) {
        super(json, TankAttribute.class);
        lastUseMs = json.optLong("last_action_time", 0);
    }

    public int getDurability() {
        return getInteger(TankAttribute.DURABILITY);
    }

    public int getRange() {
        return getInteger(TankAttribute.RANGE);
    }

    public int getGold() {
        return getInteger(TankAttribute.GOLD);
    }

    public int getActions() {
        return getInteger(TankAttribute.ACTIONS);
    }

    public int getBounty() {
        return getInteger(TankAttribute.BOUNTY);
    }

    public boolean isDead() {
        return getBoolean(TankAttribute.DEAD);
    }


    public void setDurability(int durability) {
        set(TankAttribute.DURABILITY, durability);
    }

    public void setRange(int range) {
        set(TankAttribute.RANGE, range);
    }

    public void setGold(int gold) {
        set(TankAttribute.GOLD, gold);
    }

    public void setActions(int actions) {
        set(TankAttribute.ACTIONS, actions);
    }

    public void setBounty(int bounty) {
        set(TankAttribute.BOUNTY, bounty);
    }

    public void setDead(boolean dead) {
        set(TankAttribute.DEAD, dead);
    }

    @Override
    public long getLastUsage(String rule) {
        return lastUseMs;
    }

    @Override
    public void setLastUsage(String rule, long time) {
        lastUseMs = time;
    }

    @Override
    public JSONObject toJson() {
        return super.toJson().put("last_action_time", lastUseMs);
    }
}
