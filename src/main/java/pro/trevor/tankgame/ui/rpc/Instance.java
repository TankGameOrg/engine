package pro.trevor.tankgame.ui.rpc;

import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.RulesetRegistry;
import pro.trevor.tankgame.log.ApiLogBook;
import pro.trevor.tankgame.state.State;

import java.util.Optional;

public class Instance {
    private final String version;
    private final ApiLogBook logbook;

    public Instance(String version) {
        this.version = version;
        Optional<Api> oApi = RulesetRegistry.createApi(version);
        if (oApi.isEmpty()) {
            throw new Error(String.format("Version `%s` does not exist", version));
        }
        this.logbook = new ApiLogBook(oApi.get());
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
}
