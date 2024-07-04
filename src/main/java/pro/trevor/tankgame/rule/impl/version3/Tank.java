package pro.trevor.tankgame.rule.impl.version3;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "TankV3")
public class Tank extends GenericTank {

    public Tank(JSONObject json) {
        super(json);
    }

    public int getDurability() {
        return Attribute.DURABILITY.unsafeFrom(this);
    }

    public int getRange() {
        return Attribute.RANGE.unsafeFrom(this);
    }

    public int getGold() {
        return Attribute.GOLD.unsafeFrom(this);
    }

    public int getActions() {
        return Attribute.ACTION_POINTS.unsafeFrom(this);
    }

    public int getBounty() {
        return Attribute.BOUNTY.unsafeFrom(this);
    }

    public boolean isDead() {
        return Attribute.DEAD.unsafeFrom(this);
    }

    public void setDurability(int durability) {
        Attribute.DURABILITY.to(this, durability);
    }

    public void setRange(int range) {
        Attribute.RANGE.to(this, range);
    }

    public void setGold(int gold) {
        Attribute.GOLD.to(this, gold);
    }

    public void setActions(int actions) {
        Attribute.ACTION_POINTS.to(this, actions);
    }

    public void setBounty(int bounty) {
        Attribute.BOUNTY.to(this, bounty);
    }

    public void setDead(boolean dead) {
        Attribute.DEAD.to(this, dead);
    }
}
