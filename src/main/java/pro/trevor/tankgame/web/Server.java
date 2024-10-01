package pro.trevor.tankgame.web;

import org.json.JSONObject;
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.ui.rpc.Interface;
import pro.trevor.tankgame.ui.rpc.RpcHandler;

import java.io.File;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api")
public class Server {


    private final File path = new File(System.getenv().getOrDefault("TANK_GAMES_FOLDER", "."));
    private final WebInterface webInterface = new WebInterface(path);
    private final Lock lock = new ReentrantLock();

    // Functions acting on all games
    @GetMapping("/games")
    public JSONObject getAllGames() {
        JSONObject response = new JSONObject();
        lock.lock();
        Set<String> ids = webInterface.getInstanceIds();
        // TODO
        lock.unlock();
        return response(response);
    }

    @PostMapping("/games/reload")
    public JSONObject reloadAllGames() {
        lock.lock();
        webInterface.resetAll();
        lock.unlock();
        return response("ok");
    }

    // Functions acting on a single game
    @GetMapping("/game/{gameName}")
    public JSONObject getGame(@PathVariable(name = "gameName") String gameName) {
        lock.lock();
        // TODO
        lock.unlock();
        return response("ok");
    }

    @PostMapping("/game/{gameName}/reload")
    public JSONObject reloadGame(@PathVariable(name = "gameName") String gameName) {
        lock.lock();
        webInterface.reset(gameName);
        lock.unlock();
        return response("ok");
    }

    @GetMapping("/game/{gameName}/turn/{turnId}")
    public JSONObject getGameTurn(@PathVariable(name = "gameName") String gameName, @PathVariable(name = "turnId") int turnId) {
        lock.lock();
        // TODO make this in the right format
        JSONObject response = webInterface.getInstance(gameName).getLogbook().getState(turnId).getState().toJson();
        lock.unlock();
        return response(response);
    }

    @PostMapping("/game/{gameName}/turn")
    public JSONObject postGameTurn(@PathVariable(name = "gameName") String gameName, @RequestBody JSONObject logEntry) {
        lock.lock();
        webInterface.getInstance(gameName).getLogbook().ingestLogEntry(new LogEntry(logEntry));
        lock.unlock();
        return response("ok");
    }

    @GetMapping("/game/{gameName}/possible-actions/{playerName}/{lastTurnId}")
    public JSONObject getPossibleActions(@PathVariable(name = "gameName") String gameName, @PathVariable(name = "playerName") String playerName, @PathVariable(name = "lastTurnId") int turn) {
        lock.lock();
        // TODO
        lock.unlock();
        return response("ok");
    }

    // Engine functions
    @GetMapping("/engine/")
    public JSONObject getAllEngines() {
        lock.lock();
        // TODO
        lock.unlock();
        return response("ok");
    }

    @PostMapping("/engine/game-version/{gameVersion}")
    public JSONObject setEngineVersion(@PathVariable(name = "gameVersion") String gameVersion) {
        lock.lock();
        // TODO
        lock.unlock();
        return response("ok");
    }


    // Helper functions
    private JSONObject response(String message) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private JSONObject response(JSONObject base) {
        return base.put("success", true);
    }

    private JSONObject error(String message) {
        return new JSONObject().put("success", false).put("error", message);
    }

}