package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Player;

import java.util.*;

public class GenericTank extends GenericElement implements ITickElement, IPlayerElement, IUnit, IElement {

    public GenericTank(String player, Position position, Map<String, Object> defaults) {
        super(defaults);
        Attribute.POSITION.to(this, position);
        Attribute.NAME.to(this, player);
    }

    public GenericTank(JSONObject json) {
        super(json);
    }

    @Override
    public Player getPlayer() {
        return Attribute.PLAYER.unsafeFrom(this);
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }
}
