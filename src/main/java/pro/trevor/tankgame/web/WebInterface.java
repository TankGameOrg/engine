package pro.trevor.tankgame.web;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.actions.PossibleAction;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.ui.rpc.Instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebInterface {

    private final Map<String, Instance> instances;

    public WebInterface() {
        instances = new HashMap<>();
    }

    public Instance getInstance(String id) {
        Instance instance = instances.get(id);
        if (instance == null) {
            throw new Error("Instance does not exist");
        }
        return instance;
    }

    public void newInstance(String id, String version) {
        instances.put(id, new Instance(version));
    }

    public Instance removeInstance(String id) {
        return instances.remove(id);
    }

    public void ingestAction(String id, LogEntry logEntry) {
        getInstance(id).getLogbook().ingestLogEntry(logEntry);
    }

    public List<PossibleAction> possibleActions(String id, String player) {
        return getInstance(id).getCurrentApi().getPossibleActions(new PlayerRef(player));
    }

    public List<PlayerRuleError> isActionApplicable(String id, LogEntry logEntry) {
        return getInstance(id).getCurrentApi().canIngestAction(logEntry);
    }

    public void reset() {
        instances.clear();
    }

}
