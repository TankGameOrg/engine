package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.state.board.Element;
import pro.trevor.tankgame.state.board.IUnit;
import pro.trevor.tankgame.util.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "Wall")
public class Wall extends Element implements IUnit {

    public Wall(Position position, int initialDurability) {
        super();
        put(Attribute.DURABILITY, initialDurability);
        put(Attribute.POSITION, position);
    }

    public Wall(JSONObject json) {
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
