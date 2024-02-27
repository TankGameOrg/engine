package state;

import state.board.Board;

public class State {

    private final Board board;
    private int tick;

    public State(int boardWidth, int boardHeight) {
        this.board = new Board(boardWidth, boardHeight);
        this.tick = 0;
    }

    public void noop() {}

    public Board getBoard() {
        return board;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
