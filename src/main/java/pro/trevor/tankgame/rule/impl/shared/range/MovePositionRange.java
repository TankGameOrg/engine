package pro.trevor.tankgame.rule.impl.shared.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Set;

public class MovePositionRange extends FunctionVariableRange<GenericTank, Position> {

    public MovePositionRange(String name) {
        super(name, (state, tank) -> getMoveable(state, tank.getPosition()));
    }

    private static Set<Position> getMoveable(State state, Position start) {
        Set<Position> output = new HashSet<>();
        for (Position pos : Util.allAdjacentPositions(start)) {
            if (Util.canMoveTo(state, start, pos)) {
                output.add(pos);
            }
        }
        return output;
    }

    @Override
    public String getDataType() {
        return "position";
    }
}
