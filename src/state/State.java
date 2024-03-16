package state;

import rule.type.IMetaElement;
import state.board.Board;
import state.meta.Council;
import state.meta.None;
import state.meta.Player;

import java.util.*;

public class State {

    private final List<IMetaElement> metaTickElements;
    private final Board board;
    private final Council council;
    private final Map<String, Player> players;

    private int tick;

    public State(int boardWidth, int boardHeight, Set<String> players) {
        this.board = new Board(boardWidth, boardHeight);
        this.players = new HashMap<>();
        for (String player : players) {
            this.players.put(player, new Player(player));
        }
        this.council = new Council();
        this.tick = 0;

        this.metaTickElements = new ArrayList<>(3);
        this.metaTickElements.add(board);
        this.metaTickElements.add(council);
        this.metaTickElements.add(new None());
    }

    public List<IMetaElement> getTickElements() {
        return metaTickElements;
    }

    public Board getBoard() {
        return board;
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
