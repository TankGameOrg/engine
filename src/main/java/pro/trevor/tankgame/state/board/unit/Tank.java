package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.state.board.Element;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.IUnit;
import pro.trevor.tankgame.util.Position;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "Tank")
public class Tank extends Element implements IUnit, IElement {

    public Tank(PlayerRef player, Position position, Map<Attribute<?>, Object> defaults) {
        super(defaults);
        put(Attribute.POSITION, position);
        put(Attribute.PLAYER_REF, player);
    }

    public Tank(JSONObject json) {
        super(json);
    }

    public PlayerRef getPlayerRef() {
        return getUnsafe(Attribute.PLAYER_REF);
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }
}
