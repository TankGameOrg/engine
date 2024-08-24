package pro.trevor.tankgame.rule.definition.range;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

public abstract class GenericTankFunctionVariableRange<T> extends FunctionVariableRange<PlayerRef, T> {
    GenericTankFunctionVariableRange(String name, BiFunction<State, GenericTank, Set<T>> generator) {
        super(name, (state, playerRef) -> {
            Optional<GenericTank> optionalTank = state.getTankForPlayerRef(playerRef);
            if(optionalTank.isEmpty()) {
                return new HashSet<>();
            }

            return generator.apply(state, optionalTank.get());
        });
    }
}
