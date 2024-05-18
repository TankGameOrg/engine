package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

public class GoldMine extends WalkableFloor {
    public GoldMine(Position position) {
        super(position);
    }

    @Override
    public char toBoardCharacter() {
        return 'G';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "gold_mine");
        return output;
    }
}
