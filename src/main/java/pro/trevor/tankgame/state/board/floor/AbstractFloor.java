package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

public abstract class AbstractFloor extends GenericElement implements IFloor {

    public AbstractFloor(Position position) {
        super();
        put(Attribute.POSITION, position);
    }

    public AbstractFloor(JSONObject json) {
        super(json);
    }

}
