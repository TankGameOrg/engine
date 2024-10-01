package pro.trevor.tankgame.ui.rpc;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.RulesetRegistry;
import pro.trevor.tankgame.log.ApiLogBook;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.openhours.OpenHours;

import java.util.Optional;

public class EngineInstance implements IJsonObject {

    private static final String BUILD_INFO_KEY = "buildInfo";
    private static final String ENGINE_INFO_KEY = "buildTime";

    private static final String GAME_INFO_KEY = "game";
    private static final String VERSION_KEY = "gameVersion";
    private static final String ID_KEY = "id";
    private static final String STATE_STRING_KEY = "state";
    private static final String STATUS_TEXT_KEY = "statusText";

    private static final String GAME_SETTINGS_KEY = "gameSettings";

    private static final String INITIAL_STATE_KEY = "initialGameState";
    private static final String LOGBOOK_KEY = "logbook";
    private static final String OPEN_HOURS_KEY = "openHours";

    private final String id;
    private final String version;
    private final ApiLogBook logbook;
    private final OpenHours openHours;
    private final Settings settings;

    public EngineInstance(JSONObject json) {

        JSONObject gameInfoJson = json.getJSONObject(GAME_INFO_KEY);

        this.id = gameInfoJson.getString(ID_KEY);
        this.version = gameInfoJson.getString(VERSION_KEY);

        this.openHours = new OpenHours(json.getJSONObject(OPEN_HOURS_KEY));
        this.settings = new Settings(json.getJSONObject(GAME_SETTINGS_KEY));

        Optional<Api> oApi = RulesetRegistry.createApi(this.version);
        if (oApi.isEmpty()) {
            throw new Error(String.format("Version `%s` does not exist", this.version));
        }
        this.logbook = new ApiLogBook(oApi.get());
        this.logbook.currentState().setState(new State(json.getJSONObject(INITIAL_STATE_KEY)));
        JSONArray logEntries = json.getJSONArray(LOGBOOK_KEY);
        for (int i = 0; i < logEntries.length(); ++i) {
            this.logbook.ingestLogEntry(new LogEntry(logEntries.getJSONObject(i)));
        }
    }

    public EngineInstance(String id, String version, OpenHours openHours, Settings settings) {
        this.id = id;
        this.version = version;
        Optional<Api> oApi = RulesetRegistry.createApi(version);
        if (oApi.isEmpty()) {
            throw new Error(String.format("Version `%s` does not exist", version));
        }
        this.logbook = new ApiLogBook(oApi.get());
        this.openHours = openHours;
        this.settings = settings;
    }

    public EngineInstance(String id, String version) {
        this(id, version, new OpenHours(), new Settings());
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public Api getCurrentApi() {
        return logbook.currentState();
    }

    public State getCurrentState() {
        return logbook.currentState().getState();
    }

    public ApiLogBook getLogbook() {
        return logbook;
    }

    public OpenHours getOpenHours() {
        return openHours;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = toClientJson();
        output.put(INITIAL_STATE_KEY, logbook.getState(0));
        return output;
    }

    public JSONObject toClientJson() {
        JSONObject output = new JSONObject();

        // TODO resolve this information
        output.put(BUILD_INFO_KEY, "Unknown");
        output.put(ENGINE_INFO_KEY, "Unknown");

        output.put(GAME_SETTINGS_KEY, settings.toJson());
        output.put(OPEN_HOURS_KEY, openHours.toJson());

        JSONArray logEntries = new JSONArray();
        for (int i = 0; i < logbook.logEntriesSize(); ++i) {
            logEntries.put(logbook.getLogEntry(i).toJson());
        }
        output.put(LOGBOOK_KEY, logEntries);

        JSONObject gameInfoJson = new JSONObject();
        gameInfoJson.put(ID_KEY, id);
        gameInfoJson.put(VERSION_KEY, version);
        gameInfoJson.put(STATE_STRING_KEY, this.getCurrentState().getUnsafe(Attribute.RUNNING) ? "running" : "game-over");
        gameInfoJson.put(STATUS_TEXT_KEY, this.getCurrentState().getUnsafe(Attribute.RUNNING) ? "Playing" :
                String.format("The game is over; %s won!", this.getCurrentState().getUnsafe(Attribute.WINNER)));
        output.put(GAME_INFO_KEY, gameInfoJson);

        return output;
    }
}
