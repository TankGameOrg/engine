package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

import java.util.function.BiPredicate;

public class ConditionallyWalkableFloor extends AbstractPositionedFloor {

    private final BiPredicate<AbstractPositionedFloor, Board> predicate;

    public ConditionallyWalkableFloor(Position position, BiPredicate<AbstractPositionedFloor, Board> predicate) {
        super(position);
        this.predicate = predicate;
    }


    @Override
    public boolean isWalkable(Board board) {
        return predicate.test(this, board);
    }


    @Override
    public char toBoardCharacter() {
        return '~';
    }
}
