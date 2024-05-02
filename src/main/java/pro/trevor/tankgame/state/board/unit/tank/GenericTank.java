package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericUnit;

import java.util.*;

public class GenericTank<E extends Enum<E> & IAttribute> extends GenericUnit<E> implements IMovable, ITickElement, IPlayerElement {

    protected final String player;

    public GenericTank(String player, Position position, Map<E, Object> defaults) {
        super(position, defaults);
        this.player = player;
    }

    public GenericTank(JSONObject json, IAttributeDecoder<E> attributeDecoder) {
        super(json, attributeDecoder);
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
        return getPlayer();
    }
}
