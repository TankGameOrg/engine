package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.IElement;

public interface IFloor extends IElement {
    boolean isWalkable(Board board);
}
