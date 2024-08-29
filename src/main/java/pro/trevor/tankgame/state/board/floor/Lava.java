package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "Lava")
public class Lava extends WalkableFloor {
    public Lava(Position position, int damage) {
        super(position);
        put(Attribute.DAMAGE, damage);
    }

    public Lava(JSONObject json) {
        super(json);
    }

    public int getDamage() {
        return getUnsafe(Attribute.DAMAGE);
    }
}
