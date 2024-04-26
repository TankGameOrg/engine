package pro.trevor.tankgame.state.board.unit.tank;

import pro.trevor.tankgame.state.board.unit.IUnit;

public interface IWallet extends IUnit {
    int getGold();
    void setGold(int gold);
}
