package state.board.unit;

import state.board.Position;

public class NullUnit implements IUnit, IWalkable {

    public NullUnit() {
    }

    @Override
    public Position getPosition() {
        throw new Error("Attempted to get position of null unit");
    }

    @Override
    public String toString() {
        return "_";
    }
}
