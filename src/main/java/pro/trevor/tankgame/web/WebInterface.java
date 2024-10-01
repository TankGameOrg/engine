package pro.trevor.tankgame.web;

import org.json.JSONObject;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.actions.PossibleAction;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.ui.rpc.EngineInstance;
import pro.trevor.tankgame.util.JsonReader;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebInterface {

    private final Map<String, EngineInstance> instances;
    private final File directory;

    public WebInterface(File directory) {
        instances = new HashMap<>();
        assert directory != null && directory.exists();
        assert directory.isDirectory();
        this.directory = directory;
    }

    public EngineInstance getInstance(String id) {
        EngineInstance instance = instances.get(id);
        if (instance == null) {
            throw new Error("Instance does not exist");
        }
        return instance;
    }

    public void loadInstance(String id) {
        File gameFile = new File(directory, id + ".json");
        if (!gameFile.exists()) {
            throw new Error(String.format("Game file `%s` does not exist", id + ".json"));
        }
        JSONObject gameJson = JsonReader.readJson(gameFile);
        EngineInstance instance = new EngineInstance(gameJson);
    }

    public void saveInstance(String id) {
        File gameFile = new File(directory, id + ".json");
        EngineInstance instance = getInstance(id);

    }

    public void newInstance(String id, String version) {
        instances.put(id, new EngineInstance(id, version));
    }

    public EngineInstance removeInstance(String id) {
        return instances.remove(id);
    }

    public List<PlayerRuleError> ingestAction(String id, LogEntry logEntry) {
        return getInstance(id).getLogbook().ingestLogEntry(logEntry);
    }

    public List<PossibleAction> possibleActions(String id, String player) {
        return getInstance(id).getCurrentApi().getPossibleActions(new PlayerRef(player));
    }

    public List<PlayerRuleError> isActionApplicable(String id, LogEntry logEntry) {
        return getInstance(id).getCurrentApi().canIngestAction(logEntry);
    }

    public void reset(String id) {
        saveInstance(id);
        removeInstance(id);
        loadInstance(id);
    }

    public void resetAll() {
        Set<String> keys = instances.keySet();
        for (String id : keys) {
            saveInstance(id);
        }
        instances.clear();
        for (String id : keys) {
            loadInstance(id);
        }
    }

}
