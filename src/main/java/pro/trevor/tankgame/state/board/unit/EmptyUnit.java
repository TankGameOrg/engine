package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;

@JsonType(name = "EmptyUnit")
public class EmptyUnit extends GenericElement implements IUnit {

    public EmptyUnit(Position position) {
        super();
        Attribute.POSITION.to(this, position);
    }

    public EmptyUnit(JSONObject json) {
        super(json);
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }
}
