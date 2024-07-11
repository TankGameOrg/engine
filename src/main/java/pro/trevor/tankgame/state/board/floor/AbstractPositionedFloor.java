package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Position;

public abstract class AbstractPositionedFloor implements IFloor {

    protected final Position position;

    public AbstractPositionedFloor(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return position.toString();
    }
}
