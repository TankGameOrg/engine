package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.Ruleset;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Optional;

import pro.trevor.tankgame.log.LogEntry;

public class PlayerRuleContext {
    private final State state;
    private final PlayerRef playerRef;
    private final IPlayerRule rule;
    private final Ruleset ruleset;
    private final Optional<LogEntry> logEntry;

    public PlayerRuleContext(State state, PlayerRef playerRef, IPlayerRule rule, Ruleset ruleset) {
        this(state, playerRef, rule, ruleset, Optional.empty());
    }

    public PlayerRuleContext(State state, PlayerRef playerRef, IPlayerRule rule, Ruleset ruleset, LogEntry logEntry) {
        this(state, playerRef, rule, ruleset, Optional.of(logEntry));
    }

    private PlayerRuleContext(State state, PlayerRef playerRef, IPlayerRule rule, Ruleset ruleset, Optional<LogEntry> logEntry) {
        this.state = state;
        this.playerRef = playerRef;
        this.rule = rule;
        this.ruleset = ruleset;
        this.logEntry = logEntry;
    }

    public State getState() {
        return state;
    }

    public PlayerRef getPlayerRef() {
        return playerRef;
    }

    public IPlayerRule getRule() {
        return rule;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public Optional<LogEntry> getLogEntry() {
        return logEntry;
    }
}
