package pro.trevor.tankgame.state.range;

import pro.trevor.tankgame.state.board.unit.Tank;

import java.util.HashSet;

public class TankRange<S> extends FunctionVariableRange<S, Tank>{
    public TankRange(String name) {
        super(name, (state, subject) -> new HashSet<>(state.getBoard().gatherUnits(Tank.class)));
    }
}
