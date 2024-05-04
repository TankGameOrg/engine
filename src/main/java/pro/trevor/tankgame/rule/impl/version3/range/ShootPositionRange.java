package pro.trevor.tankgame.rule.impl.version3.range;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.range.FunctionVariableRange;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.Util;
import pro.trevor.tankgame.util.function.ITriPredicate;

import java.util.HashSet;
import java.util.Set;

public class ShootPositionRange extends FunctionVariableRange<Tank, Position> {

    public ShootPositionRange(String name, ITriPredicate<State, Position, Position> lineOfSight) {
        super(name, (state, tank) -> getShootable(lineOfSight, state, tank.getPosition(), tank.getRange()));
    }

    private static Set<Position> getShootable(ITriPredicate<State, Position, Position> lineOfSight, State state, Position center, int range) {
        Set<Position> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(center, range)) {
            if (LineOfSight.hasLineOfSightV3(state, center, pos)) {
                output.add(pos);
            }
        }
        return output;
    }
}
