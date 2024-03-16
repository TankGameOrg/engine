package state.board.floor;

import state.board.Position;

public abstract class AbstractPositionedFloor implements IFloor {

    private final Position position;

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
