package state;

import state.board.Board;
import state.meta.Council;
import state.meta.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class State {

    private final Board board;
    private final Map<String, Player> players;
    private final Council council;
    private int tick;

    public State(int boardWidth, int boardHeight, Set<String> players) {
        this.board = new Board(boardWidth, boardHeight);
        this.players = new HashMap<>();
        for (String player : players) {
            this.players.put(player, new Player(player));
        }
        this.council = new Council();
        this.tick = 0;
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
