package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

public class UnwalkableFloor extends AbstractPositionedFloor {

    private static final String typeValue = "unwalkable";
    private final JSONObject jsonFromConstruction;

    public UnwalkableFloor(Position position) {
        super(position);
        jsonFromConstruction = new JSONObject();
        jsonFromConstruction.put("type", typeValue);
    }

    public UnwalkableFloor(Position p, JSONObject json) {
        super(p);
        jsonFromConstruction = json;
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
    public JSONObject toJson() {
        JSONObject output = jsonFromConstruction;
        return output;
    }
}
