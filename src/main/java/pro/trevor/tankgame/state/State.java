package pro.trevor.tankgame.state;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.*;

@JsonType(name = "State")
public class State extends AttributeObject implements IJsonObject {
    public State(Board board, Council council) {
        Attribute.BOARD.to(this, board);
        Attribute.COUNCIL.to(this, council);
        Attribute.TICK.to(this, 0);
        Attribute.RUNNING.to(this, true);
        Attribute.WINNER.to(this, "");

        AttributeList<Player> players = new AttributeList<>();
        players.addAll(board.gatherUnits(GenericTank.class).stream().map(GenericTank::getPlayer).toList());
        players.addAll(council.getCouncillors());
        players.addAll(council.getSenators());

        Attribute.PLAYERS.to(this, players);
    }

    public State(JSONObject json) {
        super(json);
    }

    public State(int boardWidth, int boardHeight) {
        this(new Board(boardWidth, boardHeight), new Council());
    }

    public List<IMetaElement> getMetaElements() {
        return List.of(getBoard(), getCouncil());
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

    public Optional<Player> getPlayer(String name) {
        for (Player player : getPlayers()) {
            if (player.getName().equals(name)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }
}
