package pro.trevor.tankgame.state.board.floor;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;

@JsonType(name = "DestructibleFloor")
public class DestructibleFloor extends AbstractFloor {

    public DestructibleFloor(Position position, int durability, int maxDurability) {
        super(position);
        put(Attribute.DURABILITY, durability);
        put(Attribute.MAX_DURABILITY, maxDurability);
        if(durability == 0) {
            put(Attribute.DESTROYED, true);
        }
    }

    public DestructibleFloor(JSONObject json) {
        super(json);
        assert has(Attribute.DURABILITY);
        assert has(Attribute.MAX_DURABILITY);
        assert getUnsafe(Attribute.DURABILITY) <= getUnsafe(Attribute.MAX_DURABILITY);
        assert getUnsafe(Attribute.DURABILITY) >= 0;
        assert getUnsafe(Attribute.DURABILITY) != 0 || has(Attribute.DESTROYED);
    }

    @Override
    public char toBoardCharacter() {
        return '~';
    }

    @Override
    public boolean isWalkable(Board board) {
        return !get(Attribute.DESTROYED).orElse(false);
    }

}
