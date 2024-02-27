package state.board.floor;

import state.board.Board;
import state.board.IElement;
import state.board.IPositioned;

public interface IFloor extends IElement, IPositioned {
    boolean isWalkable(Board board);
}
