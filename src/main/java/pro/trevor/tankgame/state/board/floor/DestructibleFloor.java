package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

public class DestructibleFloor extends GenericElement implements IFloor {

    private static final String typeValue = "destructible_floor";

    public DestructibleFloor(Position p, JSONObject json) {
        super(json);
    }

    @Override
    public Position getPosition() {
        return getPosition();
    }

    @Override
    public char toBoardCharacter() {
        return '~';
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("type", typeValue);
        return json;
    }

    @Override
    public boolean isWalkable(Board board) {
        return Attribute.DURABILITY.unsafeFrom(this) > 0;
    }

}
