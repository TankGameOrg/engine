package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "Wall")
public class BasicWall extends GenericElement implements IUnit {

    public BasicWall(Position position, int initialDurability) {
        super();
        put(Attribute.DURABILITY, initialDurability);
        put(Attribute.POSITION, position);
    }

    public BasicWall(JSONObject json) {
        super(json);
    }

    public int getDurability() {
        return get(Attribute.DURABILITY).orElse(0);
    }

    public void setDurability(int durability) {
        put(Attribute.DURABILITY, durability);
    }

    @Override
    public char toBoardCharacter() {
        return (char) ('0' + getDurability());
    }
}
