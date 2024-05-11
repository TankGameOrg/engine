package pro.trevor.tankgame.state.board.floor;

import java.util.function.BiPredicate;
import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

public class ConditionallyWalkableFloor extends AbstractPositionedFloor {

    protected final BiPredicate<AbstractPositionedFloor, Board> predicate;

    public ConditionallyWalkableFloor(
            Position position, BiPredicate<AbstractPositionedFloor, Board> predicate) {
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

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "conditional");
        return output;
    }
}
