package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "GenericTank")
public class GenericTank extends GenericElement implements IPlayerElement, IUnit, IElement {

    public GenericTank(PlayerRef player, Position position, Map<Attribute<?>, Object> defaults) {
        super(defaults);
        put(Attribute.POSITION, position);
        put(Attribute.PLAYER_REF, player);
    }

    public GenericTank(JSONObject json) {
        super(json);
    }

    @Override
    public PlayerRef getPlayerRef() {
        return getUnsafe(Attribute.PLAYER_REF);
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }
}
