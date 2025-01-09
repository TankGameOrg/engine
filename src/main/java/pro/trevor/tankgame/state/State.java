package pro.trevor.tankgame.state;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.attribute.Entity;
import pro.trevor.tankgame.attribute.ListEntity;
import pro.trevor.tankgame.attribute.AttributeEntity;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;
import java.util.stream.Stream;

@JsonType(name = "State")
public class State extends AttributeEntity implements IJsonObject {

    public State(Board board, Council council, ListEntity<Player> players) {
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

    public ListEntity<Player> getPlayers() {
        return getUnsafe(Attribute.PLAYERS);
    }

    public Optional<Player> getPlayer(PlayerRef playerRef) {
        return playerRef.toPlayer(this);
    }

    @Override
    public Stream<Object> gather() {
        Stream<Object> result = Stream.empty();
        for (Object value : attributes.values()) {
            if (value instanceof IJsonObject) {
                result = Stream.concat(result, Stream.of(value));
            }
            if (value instanceof Entity entity) {
                result = Stream.concat(result, Stream.of(entity.gather()));
            }
        }
        return result;
    }

    /**
     * Search the state for a tank controlled by the same player (ref)
     */
    public Optional<Tank> getTankForPlayerRef(PlayerRef playerRef) {
        return gather(Tank.class)
                .filter((t) -> t.getPlayerRef().equals(playerRef))
                .findFirst();
    }
}
