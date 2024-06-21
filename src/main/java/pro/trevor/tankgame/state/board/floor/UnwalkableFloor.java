package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "UnwalkableFloor")
public class UnwalkableFloor extends AbstractFloor {

    public UnwalkableFloor(Position position) {
        super(position);
    }

    public UnwalkableFloor(JSONObject json) {
        super(json);
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
