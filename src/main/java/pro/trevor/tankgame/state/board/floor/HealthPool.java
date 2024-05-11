package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.board.Position;

public class HealthPool extends StandardFloor {
    private int regenAmount;

    public HealthPool(Position position, int regenAmount) {
        super(position);
        this.regenAmount = regenAmount;
    }

    public int getRegenAmount() {
        return regenAmount;
    }

    @Override
    public char toBoardCharacter() {
        return 'H';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "health_pool");
        output.put("regen_amount", regenAmount);
        return output;
    }

}
