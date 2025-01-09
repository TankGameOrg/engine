package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.util.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "ImpassibleFloor")
public class ImpassibleFloor extends AbstractFloor {

    public ImpassibleFloor(Position position) {
        super(position);
    }

    public ImpassibleFloor(JSONObject json) {
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
