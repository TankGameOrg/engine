package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DonateTankRange extends GenericTankFunctionVariableRange<GenericTank> {
    public DonateTankRange(String name) {
        super(name, (state, tank) -> getTanksInRange(state, tank.getPosition(), tank.getOrElse(Attribute.RANGE, 0)));
    }

    private static Set<GenericTank> getTanksInRange(State state, Position center, int range) {
        Set<GenericTank> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(state.getBoard(), center, range)) {
            Optional<IUnit> unit = state.getBoard().getUnit(pos);
            if (unit.isPresent() && unit.get() instanceof GenericTank tank) {
                output.add(tank);
            }
        }
        return output;
    }

    @Override
    public String getJsonDataType() {
        return "tank";
    }

    @Override
    public Class<GenericTank> getBoundClass() {
        return GenericTank.class;
    }
}
