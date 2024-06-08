package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

public class GoldMine extends WalkableFloor {

    public GoldMine(Position position) {
        super(position);
    }

    public GoldMine(JSONObject json) {
        super(json);
    }

    @Override
    public char toBoardCharacter() {
        return 'G';
    }
}
