package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

public class Wall extends AbstractDurable {

    public static int INITIAL_HEALTH = 3;


    public Wall(Position position) {
        super(position, INITIAL_HEALTH);
    }

    public Wall(Position position, int initialHealth) {
        super(position, initialHealth);
    }

    @Override
    public String toString() {
        return position.toString();
    }


    @Override
    public char toBoardCharacter() {
        return (char) ('0' + getDurability());
    }

    @Override
    public JSONObject toJsonObject() {
        JSONObject output = super.toJsonObject();
        output.put("type", "wall");
        return output;
    }
}
