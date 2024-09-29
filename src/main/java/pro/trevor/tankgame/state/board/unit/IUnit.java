package pro.trevor.tankgame.state.board.unit;

import pro.trevor.tankgame.state.board.IElement;

public interface IUnit extends Cloneable, IElement {
    Object clone();
}
