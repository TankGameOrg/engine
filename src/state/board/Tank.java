package state.board;

import rule.type.IPlayerElement;
import rule.type.ITickElement;

public class Tank extends AbstractDestroyable implements IMovable, IDestroyable, IWallet, IRanged, ITickElement, IPlayerElement {

    int actions;
    int gold;
    int range;

    boolean dead;

    public Tank(Position position, int actions, int gold, int durability, int range) {
        super(position, durability);
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
    public void handleDestruction() {

    }

    @Override
    public void onMove() {

    }

    @Override
    public String toString() {
        return "Tank{" +
                "actions=" + actions +
                ", gold=" + gold +
                ", range=" + range +
                ", dead=" + dead +
                ", position=" + position +
                ", durability=" + durability +
                '}';
    }
}
