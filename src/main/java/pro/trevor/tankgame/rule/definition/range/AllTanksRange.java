package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.state.board.unit.GenericTank;

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
