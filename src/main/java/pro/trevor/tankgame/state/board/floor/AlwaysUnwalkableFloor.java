package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
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

    @Override
    public JSONObject toJsonObject() {
        JSONObject output = new JSONObject();
        output.put("type", "unwalkable");
        return output;
    }
}
