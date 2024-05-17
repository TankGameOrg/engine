package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

public class StandardFloor extends AbstractPositionedFloor {

    public StandardFloor(Position position) {
        super(position);
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "empty");
        return output;
    }

    @Override
    public boolean isWalkable(Board board) {
        return true;
    }
}
