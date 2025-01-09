package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.state.board.Element;
import pro.trevor.tankgame.state.board.IUnit;
import pro.trevor.tankgame.util.Position;
import pro.trevor.tankgame.util.JsonType;


@JsonType(name = "EmptyUnit")
public class EmptyUnit extends Element implements IUnit {

    public EmptyUnit(Position position) {
        super();
        put(Attribute.POSITION, position);
    }

    public EmptyUnit(JSONObject json) {
        super(json);
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }
}
