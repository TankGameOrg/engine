package state.board.unit;

import rule.type.IPlayerElement;
import rule.type.ITickElement;
import state.board.Position;
import state.board.IMovable;
import state.meta.Player;

public class Tank extends AbstractDurable implements IMovable, IWallet, IRanged, ITickElement, IPlayerElement, IDurable {

    private final Player player;
    private int actions;
    private int gold;
    private int range;

    private boolean dead;

    public Tank(Player player, Position position, int actions, int gold, int durability, int range) {
        super(position, durability);
        this.player = player;
        this.actions = actions;
        this.gold = gold;
        this.range = range;
        this.dead = false;
    }

    public Tank(Position position, int actions, int gold, int durability, int range) {
        super(position, durability);
        this.player = new Player("Test");
        this.actions = actions;
        this.gold = gold;
        this.range = range;
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

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        return "T";
    }

    public String toInfoString() {
        return "Tank{" +
                "actions=" + actions +
                ", gold=" + gold +
                ", range=" + range +
                ", dead=" + dead +
                ", position=" + position +
                ", durability=" + durability +
                '}';
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
