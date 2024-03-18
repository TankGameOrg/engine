package pro.trevor.tankgame.state.board.unit;

import pro.trevor.tankgame.state.board.Position;

public class EmptyUnit implements IWalkable {

    private Position position;

    public EmptyUnit(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return position.toString();
    }

    @Override
    public char toBoardCharacter() {
        return '_';
    }
}
