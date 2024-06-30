package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.meta.Council;

public class TestState extends State {
    public TestState() {
        super(new Board(1, 1), new Council());
    }
}
