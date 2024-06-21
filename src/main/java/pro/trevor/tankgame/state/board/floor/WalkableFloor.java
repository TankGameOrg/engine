package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "WalkableFloor")
public class WalkableFloor extends AbstractFloor {

    public WalkableFloor(Position position) {
        super(position);
    }

    public WalkableFloor(JSONObject json) {
        super(json);
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }

    @Override
    public boolean isWalkable(Board board) {
        return true;
    }
}
