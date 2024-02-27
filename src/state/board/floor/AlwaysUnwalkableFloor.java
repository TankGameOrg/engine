package state.board.floor;

import state.board.Board;
import state.board.Position;

public class AlwaysUnwalkableFloor extends AbstractPositionedFloor {

    public AlwaysUnwalkableFloor(Position position) {
        super(position);
    }

    @Override
    public boolean isWalkable(Board board) {
        return false;
    }

    @Override
    public String toString() {
        return "X";
    }
}
