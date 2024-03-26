package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.Position;

public abstract class AbstractDurable implements IDurable {

    protected Position position;
    protected int durability;

    public AbstractDurable(Position position, int durability) {
        this.position = position;
        this.durability = durability;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void setDurability(int durability) {
        this.durability = durability;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("health", durability);
        return output;
    }
}
