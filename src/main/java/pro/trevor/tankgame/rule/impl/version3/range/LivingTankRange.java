package pro.trevor.tankgame.rule.impl.version3.range;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.range.FunctionVariableRange;

import java.util.stream.Collectors;

public class LivingTankRange<S> extends FunctionVariableRange<S, Tank> {
    public LivingTankRange(String name) {
        super(name, (state, subject) -> state.getBoard().gatherUnits(Tank.class)
                .stream().filter((t) -> !t.isDead()).collect(Collectors.toSet()));
    }
}