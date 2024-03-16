package state;

import rule.type.IMetaElement;
import state.board.Board;
import state.meta.Council;
import state.meta.None;
import state.meta.Player;

import java.util.*;
import java.util.stream.Collectors;

public class State {

    private final List<IMetaElement> metaElements;
    private final Board board;
    private final Council council;
    private final Map<String, Player> players;

    private int tick;

    public State(int boardWidth, int boardHeight) {
        this.board = new Board(boardWidth, boardHeight);
        this.players = new HashMap<>();
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
        if (!players.containsKey(name)) {
            players.put(name, new Player(name));
        }
    }

    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(players.get(name));
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
}
