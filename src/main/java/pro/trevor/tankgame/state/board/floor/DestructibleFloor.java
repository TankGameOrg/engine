package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;

public class DestructibleFloor extends GenericElement implements IFloor {

    private static final String typeValue = "destructible_floor";

    private final Position position;

    public DestructibleFloor(Position p, JSONObject json) {
        super(json);
        assert json.getString("type") == typeValue;
        assert Attribute.DURABILITY.in(this);
        assert Attribute.MAX_DURABILITY.in(this);
        assert Attribute.DURABILITY.unsafeFrom(this) <= Attribute.MAX_DURABILITY.unsafeFrom(this);
        assert Attribute.DURABILITY.unsafeFrom(this) >= 0;
        if (Attribute.DURABILITY.unsafeFrom(this) == 0) assert Attribute.DESTROYED.in(this);
        position = p;
    }

    @Override
    public Position getPosition() {
        return position;
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
        return !Attribute.DESTROYED.from(this).orElse(false);
    }

}
