package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.board.Position;

public class HealthPool extends StandardFloor {
    private int regenAmount;
    private static final String typeValue = "health_pool";
    private static final String regenAmountKey = "regen_amount";

    public HealthPool(Position position, int regenAmount) {
        super(position);
        this.regenAmount = regenAmount;
    }

    public HealthPool(Position position, JSONObject json)
    {
        super(position);
        this.regenAmount = json.getInt(regenAmountKey);
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
        output.put("type", typeValue);
        output.put(regenAmountKey, regenAmount);
        return output;
    }

}
