package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.state.board.Element;
import pro.trevor.tankgame.state.board.IFloor;
import pro.trevor.tankgame.util.Position;

public abstract class AbstractFloor extends Element implements IFloor {

    public AbstractFloor(Position position) {
        super();
        put(Attribute.POSITION, position);
    }

    public AbstractFloor(JSONObject json) {
        super(json);
    }

}
