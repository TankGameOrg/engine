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
        Attribute.DURABILITY.to(this, initialDurability);
        Attribute.POSITION.to(this, position);
    }

    public BasicWall(JSONObject json) {
        super(json);
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
}
