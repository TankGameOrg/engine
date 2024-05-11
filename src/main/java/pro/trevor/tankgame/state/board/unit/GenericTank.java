package pro.trevor.tankgame.state.board.unit;

import java.util.*;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.attribute.IAttribute;

public class GenericTank<E extends Enum<E> & IAttribute> extends GenericElement<E>
        implements IMovable, ITickElement, IPlayerElement, IUnit {

    private final String player;

    public GenericTank(String player, Position position, Map<E, Object> defaults) {
        super(position, defaults);
        this.player = player;
    }

    public GenericTank(JSONObject json, Class<E> type) {
        super(json, type);
        this.player = json.getString("name");
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("type", "tank");
        output.put("name", player);
        return output;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", player, super.toString());
    }
}
