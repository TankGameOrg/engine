package pro.trevor.tankgame.state;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.Util;

import java.util.*;

public class State implements IJsonObject {

    private final List<IMetaElement> metaElements;
    private final Board board;
    private final Council council;
    private final Set<String> players;

    private boolean running;
    private int tick;
    private String winner;

    public State(Board board, Council council) {
        this.board = board;
        this.players = new HashSet<>();
        this.council = council;
        this.tick = 0;
        this.running = true;
        this.winner = "";

        this.metaElements = new ArrayList<>(2);
        this.metaElements.add(board);
        this.metaElements.add(council);
    }

    public State(int boardWidth, int boardHeight) {
        this(new Board(boardWidth, boardHeight), new Council());
    }

    public List<IMetaElement> getMetaElements() {
        return metaElements;
    }

    public Board getBoard() {
        return board;
    }

    public void putPlayer(String name) {
        players.add(name);
    }

    public Set<String> getPlayers() {
        return players;
    }

    public Council getCouncil() {
        return council;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "state");
        output.put("board", board.toJson());
        output.put("council", council.toJson());
        output.put("day", tick);
        output.put("running", running);
        output.put("winner", winner);
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tick: ").append(tick).append('\n');
        sb.append("running: ").append(running).append('\n');
        if (!winner.isEmpty()) {
            sb.append("winner: ").append(winner).append('\n');
        }
        sb.append(council.toString());
        sb.append("tanks: ").append(Util.toString(board.gatherUnits(GenericTank.class), (t) -> t.toString(2)));
        sb.append('\n').append(board.toUnitString());
        sb.append('\n').append(board.toFloorString());
        return sb.toString();
    }
}
