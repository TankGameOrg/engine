package pro.trevor.tankgame.state.board.unit.tank;

import pro.trevor.tankgame.state.board.unit.IUnit;

public interface IRanged extends IUnit {

    public int getRange();
    public void setRange(int range);

}
