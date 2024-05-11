package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.IPositioned;

public interface IFloor extends IElement, IPositioned {
    boolean isWalkable(Board board);
}
