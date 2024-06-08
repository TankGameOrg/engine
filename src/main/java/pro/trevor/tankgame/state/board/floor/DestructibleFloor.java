package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;

public class DestructibleFloor extends AbstractFloor {

    public DestructibleFloor(Position position, int durability, int maxDurability) {
        super(position);
        Attribute.DURABILITY.to(this, durability);
        Attribute.MAX_DURABILITY.to(this, maxDurability);
    }

    public DestructibleFloor(JSONObject json) {
        super(json);
        assert Attribute.DURABILITY.in(this);
        assert Attribute.MAX_DURABILITY.in(this);
        assert Attribute.DURABILITY.unsafeFrom(this) <= Attribute.MAX_DURABILITY.unsafeFrom(this);
        assert Attribute.DURABILITY.unsafeFrom(this) >= 0;
        assert Attribute.DURABILITY.unsafeFrom(this) != 0 || Attribute.DESTROYED.in(this);
    }

    @Override
    public char toBoardCharacter() {
        return '~';
    }

    @Override
    public boolean isWalkable(Board board) {
        return !Attribute.DESTROYED.from(this).orElse(false);
    }

}
