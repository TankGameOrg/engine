package state.board;

public interface IMovable extends IUnit {

    void setPosition(Position position);

    void onMove();

}
