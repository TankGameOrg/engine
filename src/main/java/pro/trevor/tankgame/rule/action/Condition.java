package pro.trevor.tankgame.rule.action;

import pro.trevor.tankgame.state.State;

public interface Condition {
    Error test(State state, LogEntry entry);
}
