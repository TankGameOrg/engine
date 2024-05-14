package pro.trevor.tankgame.rule.impl.shared.range;

import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

import java.util.HashSet;

public class AllTanksRange<S> extends FunctionVariableRange<S, GenericTank> {
    public AllTanksRange(String name) {
        super(name, (state, subject) -> new HashSet<>(state.getBoard().gatherUnits(GenericTank.class)));
    }

    @Override
    public String getDataType() {
        return "tank";
    }
}
