package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.GenericTank;

import java.util.Map;

public class Tank3 extends GenericTank<TankAttribute> {
    public Tank3(String player, Position position, Map<TankAttribute, Object> defaults) {
        super(player, position, defaults);
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
}
