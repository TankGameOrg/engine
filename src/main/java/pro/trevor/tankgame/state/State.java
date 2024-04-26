package pro.trevor.tankgame.state;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.board.unit.tank.Tank;
import pro.trevor.tankgame.state.meta.None;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class State implements IJsonObject {

    private final List<IMetaElement> metaElements;
    private final Board board;
    private final Council council;
    private final Set<String> players;

    private int tick;

    public State(int boardWidth, int boardHeight) {
        this.board = new Board(boardWidth, boardHeight);
        this.players = new HashSet<>();
        this.council = new Council();
        this.tick = 0;

        this.metaElements = new ArrayList<>(3);
        this.metaElements.add(board);
        this.metaElements.add(council);
        this.metaElements.add(new None());
    }

    public List<IMetaElement> getMetaElements() {
        return metaElements;
    }

    public List<IMetaElement> getMetaElements(Class<?> c) {
        return metaElements.stream().filter((e) -> e.getClass().equals(c)).collect(Collectors.toList());
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

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "state");
        output.put("board", board.toJson());
        output.put("council", council.toJson());
        output.put("day", tick);
        JSONArray playersJson = new JSONArray();
        players.forEach(playersJson::put);
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("tick: ").append(tick).append('\n');
        sb.append(council.toString());
        sb.append("tanks: ").append(Util.toString(board.gatherUnits(Tank.class), 2));
        sb.append('\n').append(board.toUnitString());
        sb.append('\n').append(board.toFloorString());
        return sb.toString();
    }
}
