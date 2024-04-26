package pro.trevor.tankgame.state.range;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.Tank;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Set;

public class ShootPositionRange extends FunctionVariableRange<Tank, Position> {

    public ShootPositionRange(String name) {
        super(name, (state, tank) -> getShootable(state, tank.getPosition(), tank.getRange()));
    }

    private static Set<Position> getShootable(State state, Position center, int range) {
        Set<Position> output = new HashSet<>();
        for (Position pos : Util.getSpacesInRange(center, range)) {
            if (LineOfSight.hasLineOfSightV3(state, center, pos)) {
                output.add(pos);
            }
        }
        return output;
    }
}
