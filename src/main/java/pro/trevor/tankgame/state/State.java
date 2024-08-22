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
        Attribute.BOARD.to(this, board);
        Attribute.COUNCIL.to(this, council);
        Attribute.TICK.to(this, 0);
        Attribute.RUNNING.to(this, true);
        Attribute.WINNER.to(this, "");

        Attribute.PLAYERS.to(this, players);
    }

    public State(JSONObject json) {
        super(json);
    }

    public Board getBoard() {
        return Attribute.BOARD.unsafeFrom(this);
    }

    public Council getCouncil() {
        return Attribute.COUNCIL.unsafeFrom(this);
    }

    public AttributeList<Player> getPlayers() {
        return Attribute.PLAYERS.unsafeFrom(this);
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
