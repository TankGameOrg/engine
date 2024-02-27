package state.board;

public interface IFloor extends IElement {
    void moveOntoEffect(IMovable unit);
    void stayOnEffect(IUnit unit);
    void moveOffEffect(IMovable unit);
}
