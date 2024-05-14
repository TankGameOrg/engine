package pro.trevor.tankgame.rule.impl.shared;

import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

import java.util.HashSet;

public class TankRange<S> extends FunctionVariableRange<S, GenericTank> {
    public TankRange(String name) {
        super(name, (state, subject) -> new HashSet<>(state.getBoard().gatherUnits(GenericTank.class)));
    }
}
