package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "HealthPool")
public class HealthPool extends WalkableFloor {

    public HealthPool(Position position, int regenerationAmount) {
        super(position);
        this.put(Attribute.REGENERATION, regenerationAmount);
    }

    public HealthPool(JSONObject json) {
        super(json);
    }

    public int getRegenAmount() {
        return this.getUnsafe(Attribute.REGENERATION);
    }

    @Override
    public char toBoardCharacter() {
        return 'H';
    }

}
