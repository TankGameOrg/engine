package state.board.unit;

import rule.type.IPlayerElement;
import rule.type.ITickElement;
import state.board.Position;
import state.board.IMovable;
import state.meta.Player;

public class Tank extends AbstractDurable implements IMovable, IWallet, IRanged, ITickElement, IPlayerElement, IDurable, IBounty {

    private final Player player;
    private int actions;
    private int gold;
    private int range;
    private int bounty;
    private boolean dead;

    public Tank(Player player, Position position, int actions, int gold, int durability, int range, int bounty, boolean dead) {
        super(position, durability);
        this.player = player;
        this.actions = actions;
        this.gold = gold;
        this.range = range;
        this.bounty = bounty;
        this.dead = dead;
    }

    public Tank(Position position, int actions, int gold, int durability, int range) {
        super(position, durability);
        this.player = new Player("Test");
        this.actions = actions;
        this.gold = gold;
        this.range = range;
        this.bounty = 0;
        this.dead = false;
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

    public String toInfoString() {
        return "T";
    }

    @Override
    public String toString() {
        return "Tank" + position.toString();
    }

    @Override
    public Player[] getPlayers() {
        return new Player[]{player};
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }
}
