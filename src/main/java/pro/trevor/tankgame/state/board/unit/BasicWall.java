package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

import java.util.Collections;

public class BasicWall extends GenericElement implements IUnit {

    public BasicWall(Position position, int initialHealth) {
        super(position, Collections.singletonMap(Attribute.DURABILITY.getName(), initialHealth));
    }

    public BasicWall(JSONObject json) {
        super(json);
    }

    @Override
    public String toString() {
        return position.toString();
    }

    public int getDurability() {
        return Attribute.DURABILITY.from(this).orElse(0);
    }

    public void setDurability(int durability) {
        Attribute.DURABILITY.to(this, durability);
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
