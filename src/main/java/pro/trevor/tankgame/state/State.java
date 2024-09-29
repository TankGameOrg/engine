package pro.trevor.tankgame.state;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.IGatherable;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "State")
public class State extends AttributeContainer implements Cloneable, IJsonObject, IGatherable {

    public State(Board board, Council council, AttributeList<Player> players) {
        put(Attribute.BOARD, board);
        put(Attribute.COUNCIL, council);
        put(Attribute.TICK, 0);
        put(Attribute.RUNNING, true);
        put(Attribute.WINNER, "");

        put(Attribute.PLAYERS, players);
    }

    public State(JSONObject json) {
        super(json);
    }

    public Board getBoard() {
        return getUnsafe(Attribute.BOARD);
    }

    public Council getCouncil() {
        return getUnsafe(Attribute.COUNCIL);
    }

    public AttributeList<Player> getPlayers() {
        return getUnsafe(Attribute.PLAYERS);
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

    /**
     * Search the state for a tank controlled by the same player (ref)
     */
    public Optional<Tank> getTankForPlayerRef(PlayerRef playerRef) {
        return gather(Tank.class)
            .stream().filter((t) -> t.getPlayerRef().equals(playerRef))
            .findAny();
    }

    @Override
    public State clone() {
        return (State) super.clone();
    }
}
