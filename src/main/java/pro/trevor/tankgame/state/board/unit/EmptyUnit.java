package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

import java.util.HashMap;

public class EmptyUnit extends GenericElement implements IUnit {

    private static final HashMap<String, Object> EMPTY_MAP = new HashMap<>();
    private static final String typeValue = "empty";

    private final Position position;

    public EmptyUnit(Position position) {
        super(EMPTY_MAP);
        this.position = position;
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", typeValue);
        output.put("position", position.toBoardString());
        return output;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
