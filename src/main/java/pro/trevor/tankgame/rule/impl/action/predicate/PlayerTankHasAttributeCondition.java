package pro.trevor.tankgame.rule.impl.action.predicate;

import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.rule.action.Condition;
import pro.trevor.tankgame.rule.action.Error;
import pro.trevor.tankgame.rule.action.LogEntry;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Optional;

public class PlayerTankHasAttributeCondition implements Condition {

    private final Attribute<?> attribute;

    public PlayerTankHasAttributeCondition(Attribute<?> attribute) {
        this.attribute = attribute;
    }

    @Override
    public Error test(State state, LogEntry entry) {
        PlayerRef playerRef = entry.getUnsafe(Attribute.SUBJECT);
        Optional<Tank> optionalTank = state.getTankForPlayerRef(playerRef);
        if (optionalTank.isEmpty()) {
            return new Error(Error.Type.OTHER, "Player has no tank");
        }

        Tank tank = optionalTank.get();
        if (tank.has(attribute)) {
            return Error.NONE;
        } else {
            return new Error(Error.Type.OTHER, "Player tank does not have the attribute: " + attribute.getName());
        }
    }
}
