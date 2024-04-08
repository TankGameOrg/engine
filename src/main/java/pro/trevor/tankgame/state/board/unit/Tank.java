package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;

public class Tank extends AbstractDurable implements IMovable, IWallet, IRanged, ITickElement, IPlayerElement, IDurable, IBounty {

    protected final String player;
    protected int actions;
    protected int gold;
    protected int range;
    protected int bounty;
    protected boolean dead;

    public Tank(String player, Position position, int actions, int gold, int durability, int range, int bounty, boolean dead) {
        super(position, durability);
        this.player = player;
        this.actions = actions;
        this.gold = gold;
        this.range = range;
        this.bounty = bounty;
        this.dead = dead;
    }

    public int getActions() {
        return actions;
    }

    public int getGold() {
        return gold;
    }

    public int getRange() {
        return range;
    }

    @Override
    public int getBounty() {
        return bounty;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    public void setActions(int actions) {
        this.actions = actions;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        if (dead) {
            return String.format("[%s, %s, HP: %d]", player, position.toString(), durability);
        } else {
            return String.format("[%s, %s, AP: %d HP: %d R: %d G: %d]", player, position.toString(), actions, durability, range, gold);
        }
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("type", "tank");
        output.put("name", player);
        output.put("actions", actions);
        output.put("gold", gold);
        output.put("range", range);
        output.put("bounty", bounty);
        output.put("dead", dead);
        return output;
    }

    @Override
    public JSONObject toShortJson() {
        JSONObject output = new JSONObject();
        output.put("type", "tank");
        output.put("name", player);
        return output;
    }
}
