package state.board;

import state.board.Position;
import state.board.unit.IUnit;

public interface IMovable extends IUnit {

    void setPosition(Position position);

    void onMove();

}
