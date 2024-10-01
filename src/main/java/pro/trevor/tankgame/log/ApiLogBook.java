package pro.trevor.tankgame.log;

import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;

import java.util.ArrayList;
import java.util.List;

public class ApiLogBook {
    private final List<LogEntry> logEntries;
    private final List<Api> states;

    public ApiLogBook(Api base) {
        logEntries = new ArrayList<>();
        states = new ArrayList<>();
        states.add(base);
    }


    public int size() {
        return states.size();
    }

    public int logEntriesSize() {
        return logEntries.size();
    }

    public List<PlayerRuleError> ingestLogEntry(LogEntry logEntry) {
        List<PlayerRuleError> errors = states.getLast().canIngestAction(logEntry);
        if (errors.isEmpty()) {
            Api clone = states.getLast().clone();
            clone.ingestAction(logEntry);
            states.add(clone);
            logEntries.add(logEntry);
        }
        return errors;
    }

    public Api getState(int index) {
        return states.get(index);
    }

    public LogEntry getLogEntry(int index) {
        return logEntries.get(index);
    }

    public Api currentState() {
        return states.getLast();
    }
}
