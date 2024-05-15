package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.rule.impl.version3.Tank;

import java.util.stream.Collectors;

public class LivingTankRange<S> extends FunctionVariableRange<S, Tank> {
    public LivingTankRange(String name) {
        super(name, (state, subject) -> state.getBoard().gatherUnits(Tank.class)
                .stream().filter((t) -> !t.isDead()).collect(Collectors.toSet()));
    }

    @Override
    public String getDataType() {
        return "tank";
    }
}