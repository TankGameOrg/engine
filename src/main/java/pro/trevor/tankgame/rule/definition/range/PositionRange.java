package pro.trevor.tankgame.rule.definition.range;

import java.util.Set;
import java.util.stream.Collectors;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.function.ITriPredicate;

public class PositionRange extends FunctionVariableRange<GenericTank, Position> {

    public PositionRange(String name, ITriPredicate<State, GenericTank, Position> shouldIncludeTarget) {
        super(name, (state, tank) -> generateRange(state, tank, shouldIncludeTarget));
    }

    private static Set<Position> generateRange(State state, GenericTank tank, ITriPredicate<State, GenericTank, Position> shouldIncludeTarget) {
        return state.getBoard().getAllPositions()
            .stream().filter((position) -> shouldIncludeTarget.test(state, tank, position))
            .collect(Collectors.toSet());
    }

    @Override
    public String getJsonDataType() {
        return "position";
    }

    @Override
    public Class<Position> getBoundClass() {
        return Position.class;
    }
}
