package pro.trevor.tankgame.rule.action;

import pro.trevor.tankgame.state.State;

public interface Action {
    Error apply(State state, LogEntry entry);
}
