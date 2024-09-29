package pro.trevor.tankgame.state.board.floor;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.unit.IUnit;

public interface IFloor extends Cloneable, IElement {
    boolean isWalkable(Board board);
    Object clone();
}
