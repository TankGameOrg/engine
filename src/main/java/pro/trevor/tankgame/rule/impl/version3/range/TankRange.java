package pro.trevor.tankgame.rule.impl.version3.range;

import pro.trevor.tankgame.rule.impl.version3.Tank3;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

import java.util.HashSet;

public class TankRange<S> extends FunctionVariableRange<S, Tank3> {
    public TankRange(String name) {
        super(name, (state, subject) -> new HashSet<>(state.getBoard().gatherUnits(Tank3.class)));
    }
}
