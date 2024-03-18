package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

public class AlwaysUnwalkableFloor extends AbstractPositionedFloor {

    public AlwaysUnwalkableFloor(Position position) {
        super(position);
    }

    @Override
    public boolean isWalkable(Board board) {
        return false;
    }


    @Override
    public char toBoardCharacter() {
        return 'X';
    }
}
