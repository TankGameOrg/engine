package pro.trevor.tankgame.state;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.IGatherable;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "State")
public class State extends AttributeContainer implements IJsonObject, IGatherable {

    public State(Board board, Council council, AttributeList<Player> players) {
        this.put(Attribute.BOARD, board);
        this.put(Attribute.COUNCIL, council);
        this.put(Attribute.TICK, 0);
        this.put(Attribute.RUNNING, true);
        this.put(Attribute.WINNER, "");

        this.put(Attribute.PLAYERS, players);
    }

    public State(JSONObject json) {
        super(json);
    }

    public Board getBoard() {
        return this.getUnsafe(Attribute.BOARD);
    }

    public Council getCouncil() {
        return this.getUnsafe(Attribute.COUNCIL);
    }

    public AttributeList<Player> getPlayers() {
        return this.getUnsafe(Attribute.PLAYERS);
    }

    public Optional<Player> getPlayer(PlayerRef playerRef) {
        return playerRef.toPlayer(this);
    }

    @Override
    public <T> List<T> gather(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Object value : attributes.values()) {
            if (value instanceof IGatherable gatherable) {
                result.addAll(gatherable.gather(type));
            }
            if (type.isAssignableFrom(value.getClass())) {
                result.add((T) value);
            }
        }
        return result;
    }

    @Override
    public List<Object> gatherAll() {
        List<Object> result = new ArrayList<>();
        for (Object value : attributes.values()) {
            if (value instanceof IJsonObject) {
                result.add(value);
            }
            if (value instanceof IGatherable gatherable) {
                result.addAll(gatherable.gatherAll());
            }
        }
        return result;
    }
}
