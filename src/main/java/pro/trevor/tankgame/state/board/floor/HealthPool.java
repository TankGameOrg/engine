package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;

public class HealthPool extends WalkableFloor {

    public HealthPool(Position position, int regenerationAmount) {
        super(position);
        Attribute.REGENERATION.to(this, regenerationAmount);
    }

    public HealthPool(JSONObject json) {
        super(json);
    }

    public int getRegenAmount() {
        return Attribute.REGENERATION.unsafeFrom(this);
    }

    @Override
    public char toBoardCharacter() {
        return 'H';
    }

}
