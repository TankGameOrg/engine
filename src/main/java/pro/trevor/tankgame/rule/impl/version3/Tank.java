package pro.trevor.tankgame.rule.impl.version3;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.board.unit.GenericTank;

public class Tank extends GenericTank implements ICooldownPlayerElement {

    private long lastUseMs;

    public Tank(JSONObject json) {
        super(json);
        lastUseMs = json.optLong("last_action_time", 0);
    }

    public int getDurability() {
        return Attributes.DURABILITY.unsafeFrom(this);
    }

    public int getRange() {
        return Attributes.RANGE.unsafeFrom(this);
    }

    public int getGold() {
        return Attributes.GOLD.unsafeFrom(this);
    }

    public int getActions() {
        return Attributes.ACTION_POINTS.unsafeFrom(this);
    }

    public int getBounty() {
        return Attributes.BOUNTY.unsafeFrom(this);
    }

    public boolean isDead() {
        return Attributes.DEAD.unsafeFrom(this);
    }

    public void setDurability(int durability) {
        Attributes.DURABILITY.to(this, durability);
    }

    public void setRange(int range) {
        Attributes.RANGE.to(this, range);
    }

    public void setGold(int gold) {
        Attributes.GOLD.to(this, gold);
    }

    public void setActions(int actions) {
        Attributes.ACTION_POINTS.to(this, actions);
    }

    public void setBounty(int bounty) {
        Attributes.BOUNTY.to(this, bounty);
    }

    public void setDead(boolean dead) {
        Attributes.DEAD.to(this, dead);
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
