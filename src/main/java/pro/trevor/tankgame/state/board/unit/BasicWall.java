package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

import java.util.Collections;

@JsonType(name = "Wall")
public class BasicWall extends GenericElement implements IUnit {

    public BasicWall(Position position, int initialDurability) {
        super();
        this.put(Attribute.DURABILITY, initialDurability);
        this.put(Attribute.POSITION, position);
    }

    public BasicWall(JSONObject json) {
        super(json);
    }

    public int getDurability() {
        return this.get(Attribute.DURABILITY).orElse(0);
    }

    public void setDurability(int durability) {
        this.put(Attribute.DURABILITY, durability);
    }

    @Override
    public char toBoardCharacter() {
        return (char) ('0' + getDurability());
    }
}
