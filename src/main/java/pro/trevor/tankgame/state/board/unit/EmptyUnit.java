package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

import java.util.HashMap;

public class EmptyUnit extends GenericElement implements IUnit {

    private static final HashMap<String, Object> EMPTY_MAP = new HashMap<>();

    public EmptyUnit(Position position) {
        super(position, EMPTY_MAP);
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
}
