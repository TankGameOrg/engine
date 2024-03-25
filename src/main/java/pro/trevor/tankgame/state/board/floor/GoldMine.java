package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

public class GoldMine extends StandardFloor {
    public GoldMine(Position position) {
        super(position);
    }

    @Override
    public char toBoardCharacter() {
        return 'G';
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject output = new JSONObject();
        output.put("type", "gold");
        output.put("position", position.toJsonObject());
        return output;
    }
}
