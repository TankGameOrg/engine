package state.board.floor;

import state.board.unit.IWalkable;
import state.board.Position;

public class StandardFloor extends ConditionallyWalkableFloor {

    public StandardFloor(Position position) {
        super(position, (f, b) -> b.getUnit(f.getPosition()).orElse(null) instanceof IWalkable);
    }

    @Override
    public String toString() {
        return "_";
    }
}
