package pro.trevor.tankgame.rule.impl.version3.range;

import java.util.HashSet;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

public class TankRange<S> extends FunctionVariableRange<S, Tank> {
    public TankRange(String name) {
        super(name, (state, subject) -> new HashSet<>(state.getBoard().gatherUnits(Tank.class)));
    }
}
