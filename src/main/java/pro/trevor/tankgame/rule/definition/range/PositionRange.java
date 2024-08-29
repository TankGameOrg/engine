package pro.trevor.tankgame.rule.definition.range;

import java.util.Set;
import java.util.stream.Collectors;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.ITriPredicate;

public class PositionRange extends FunctionVariableRange<PlayerRef, Position> {

    public PositionRange(String name, ITriPredicate<State, PlayerRef, Position> shouldIncludeTarget) {
        super(name, (state, player) -> generateRange(state, player, shouldIncludeTarget));
    }

    private static Set<Position> generateRange(State state, PlayerRef player, ITriPredicate<State, PlayerRef, Position> shouldIncludeTarget) {
        return state.getBoard().getAllPositions()
            .stream().filter((position) -> shouldIncludeTarget.test(state, player, position))
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
