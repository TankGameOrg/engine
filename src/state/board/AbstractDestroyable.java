package state.board;

import state.board.IDestroyable;
import state.board.Position;

public abstract class AbstractDestroyable implements IDestroyable {

    protected Position position;
    protected int durability;

    public AbstractDestroyable(Position position, int durability) {
        this.position = position;
        this.durability = durability;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void setDurability(int durability) {
        this.durability = durability;
    }
}
