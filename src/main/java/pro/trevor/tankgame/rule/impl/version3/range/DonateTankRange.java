package pro.trevor.tankgame.rule.impl.version3.range;

import pro.trevor.tankgame.rule.impl.version3.Tank3;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.range.FunctionVariableRange;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DonateTankRange extends FunctionVariableRange<Tank3, Tank3> {
    public DonateTankRange(String name) {
        super(name, (state, tank) -> getTanksInRange(state, tank.getPosition(), tank.getRange()));
    }

    private static Set<Tank3> getTanksInRange(State state, Position center, int range) {
        Set<Tank3> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(center, range)) {
            Optional<IUnit> unit = state.getBoard().getUnit(pos);
            if (unit.isPresent() && unit.get() instanceof Tank3 tank) {
                output.add(tank);
            }
        }
        return output;
    }
}
