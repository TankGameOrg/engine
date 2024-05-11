package pro.trevor.tankgame.rule.impl.version3.range;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.range.FunctionVariableRange;
import pro.trevor.tankgame.util.Util;

public class DonateTankRange extends FunctionVariableRange<Tank, Tank> {
    public DonateTankRange(String name) {
        super(name, (state, tank) -> getTanksInRange(state, tank.getPosition(), tank.getRange()));
    }

    private static Set<Tank> getTanksInRange(State state, Position center, int range) {
        Set<Tank> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(center, range)) {
            Optional<IUnit> unit = state.getBoard().getUnit(pos);
            if (unit.isPresent() && unit.get() instanceof Tank tank) {
                output.add(tank);
            }
        }
        return output;
    }
}
