package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.Position;

import java.util.*;

public abstract class GenericTank extends GenericElement implements IMovable, ITickElement, IPlayerElement, IUnit {

    private final String player;

    public GenericTank(String player, Position position, Map<String, Object> defaults) {
        super(position, defaults);
        this.player = player;
    }

    public GenericTank(JSONObject json) {
        super(json);
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
