package pro.trevor.tankgame.rule.impl.version3.range;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

import java.util.stream.Collectors;

public class DeadTankRange<S> extends FunctionVariableRange<S, Tank> {
    public DeadTankRange(String name) {
        super(name, (state, subject) -> state.getBoard().gatherUnits(Tank.class)
                .stream().filter(Tank::isDead).collect(Collectors.toSet()));
    }
}