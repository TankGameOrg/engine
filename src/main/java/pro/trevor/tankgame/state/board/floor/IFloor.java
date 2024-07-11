package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.IPositioned;

public interface IFloor extends IPositioned {
    boolean isWalkable(Board board);
}
