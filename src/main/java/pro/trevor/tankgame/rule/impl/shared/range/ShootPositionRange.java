package pro.trevor.tankgame.rule.impl.shared.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;
import pro.trevor.tankgame.util.Util;
import pro.trevor.tankgame.util.function.ITriPredicate;

import java.util.HashSet;
import java.util.Set;

public class ShootPositionRange extends FunctionVariableRange<GenericTank, Position> {

    public ShootPositionRange(String name, ITriPredicate<State, Position, Position> lineOfSight) {
        super(name, (state, tank) -> getShootable(lineOfSight, state, tank.getPosition(), Attributes.RANGE.from(tank).orElse(0)));
    }

    private static Set<Position> getShootable(ITriPredicate<State, Position, Position> lineOfSight, State state, Position center, int range) {
        Set<Position> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(center, range)) {
            if (lineOfSight.test(state, center, pos)) {
                output.add(pos);
            }
        }
        return output;
    }
}
