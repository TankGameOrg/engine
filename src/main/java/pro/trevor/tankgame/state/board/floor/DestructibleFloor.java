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
        this.put(Attribute.DURABILITY, durability);
        this.put(Attribute.MAX_DURABILITY, maxDurability);
    }

    public DestructibleFloor(JSONObject json) {
        super(json);
        assert this.has(Attribute.DURABILITY);
        assert this.has(Attribute.MAX_DURABILITY);
        assert this.getUnsafe(Attribute.DURABILITY) <= this.getUnsafe(Attribute.MAX_DURABILITY);
        assert this.getUnsafe(Attribute.DURABILITY) >= 0;
        assert this.getUnsafe(Attribute.DURABILITY) != 0 || this.has(Attribute.DESTROYED);
    }

    @Override
    public char toBoardCharacter() {
        return '~';
    }

    @Override
    public boolean isWalkable(Board board) {
        return !this.get(Attribute.DESTROYED).orElse(false);
    }

}
