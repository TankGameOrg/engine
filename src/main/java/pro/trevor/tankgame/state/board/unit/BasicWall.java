package pro.trevor.tankgame.state.board.unit;

import java.util.Collections;
import org.json.JSONObject;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.attribute.DurableAttribute;

public class BasicWall extends GenericElement<DurableAttribute> implements IUnit {

    public BasicWall(Position position, int initialHealth) {
        super(position, Collections.singletonMap(DurableAttribute.DURABILITY, initialHealth));
    }

    public BasicWall(JSONObject json) {
        super(json, DurableAttribute.class);
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public int getDurability() {
        return getInteger(DurableAttribute.DURABILITY);
    }

    public void setDurability(int durability) {
        set(DurableAttribute.DURABILITY, durability);
    }

    @Override
    public char toBoardCharacter() {
        return (char) ('0' + getDurability());
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("type", "wall");
        return output;
    }
}
