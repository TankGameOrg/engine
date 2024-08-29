package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.JsonType;


@JsonType(name = "LootBox")
public class LootBox extends GenericElement implements IUnit {
    public LootBox(Position position) {
        super();
        put(Attribute.POSITION, position);
    }

    public LootBox(JSONObject json) {
        super(json);
    }

    public boolean isEmpty() {
        return has(Attribute.HAS_BEEN_LOOTED);
    }

    public void setHasBeenLooted() {
        put(Attribute.HAS_BEEN_LOOTED, true);
    }
}
