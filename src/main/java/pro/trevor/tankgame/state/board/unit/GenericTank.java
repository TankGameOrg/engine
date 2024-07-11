package pro.trevor.tankgame.state.board.unit;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.Position;

import java.util.*;

public class GenericTank extends GenericElement implements IMovable, ITickElement, IPlayerElement, IUnit {

    private static final String typeValue = "tank";
    private final String player;
    private Position position;

    public GenericTank(String player, Position position, Map<String, Object> defaults) {
        super(defaults);
        this.player = player;
        this.position = position;
    }

    public GenericTank(JSONObject json) {
        super(json);
        this.player = json.getString("name");
        this.position = new Position(json.getString("position"));
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
        output.put("type", typeValue);
        output.put("position", position.toBoardString());
        output.put("name", player);
        return output;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", player, super.toString());
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
