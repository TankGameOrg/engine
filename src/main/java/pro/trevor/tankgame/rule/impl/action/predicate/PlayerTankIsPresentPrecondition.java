package pro.trevor.tankgame.rule.impl.action.predicate;

import pro.trevor.tankgame.rule.action.Error;
import pro.trevor.tankgame.rule.action.Precondition;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Player;

import java.util.Optional;

public class PlayerTankIsPresentPrecondition implements Precondition {
    @Override
    public Error test(State state, Player player) {
        Optional<Tank> optionalTank = state.getTankForPlayerRef(player.toRef());
        if (optionalTank.isEmpty()) {
            return new Error(Error.Type.OTHER, "Player tank is absent");
        } else {
            return Error.NONE;
        }
    }
}
