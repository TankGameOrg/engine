package pro.trevor.tankgame.util;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class ContextBuilder {
    State state;
    PlayerRef subject;
    LogEntry logEntry;

    public ContextBuilder(State state, PlayerRef subject) {
        this.state = state;
        this.subject = subject;
        this.logEntry = new LogEntry();
    }

    public <T> ContextBuilder with(Attribute<T> attribute, T value) {
        logEntry.put(attribute, value);
        return this;
    }

    public ContextBuilder withTarget(Position target) {
        return with(Attribute.TARGET_POSITION, target);
    }

    public ContextBuilder withTarget(PlayerRef player) {
        return with(Attribute.TARGET_PLAYER, player);
    }

    public ContextBuilder withTarget(GenericTank tank) {
        return withTarget(tank.getPlayerRef());
    }

    public PlayerRuleContext finish() {
        return new PlayerRuleContext(state, subject, logEntry);
    }
}
