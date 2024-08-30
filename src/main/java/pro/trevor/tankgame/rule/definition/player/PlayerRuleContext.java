package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Optional;

import pro.trevor.tankgame.log.LogEntry;

public class PlayerRuleContext {
    State state;
    PlayerRef playerRef;
    Optional<LogEntry> logEntry;

    public PlayerRuleContext(State state, PlayerRef playerRef, Optional<LogEntry> logEntry) {
        this.state = state;
        this.playerRef = playerRef;
        this.logEntry = logEntry;
    }

    public State getState() {
        return state;
    }

    public PlayerRef getPlayerRef() {
        return playerRef;
    }

    public Optional<LogEntry> getLogEntry() {
        return logEntry;
    }
}
