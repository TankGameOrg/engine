package pro.trevor.tankgame.web;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import pro.trevor.tankgame.ui.rpc.Interface;
import pro.trevor.tankgame.ui.rpc.RpcHandler;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/api")
public class Server {


    private final Interface engine = new Interface(new RpcHandler());
    private final Lock lock = new ReentrantLock();

    // Functions acting on all games
    @GetMapping("/games")
    public JSONObject getAllGames() {
        return response("ok");
    }

    @PostMapping("/games/reload")
    public JSONObject reloadAllGames() {
        return response("ok");
    }

    // Functions acting on a single game
    @GetMapping("/game/{gameName}")
    public JSONObject getGame(@PathVariable(name = "gameName") String gameName) {
        return response("ok");
    }

    @PostMapping("/game/{gameName}/reload")
    public JSONObject reloadGame(@PathVariable(name = "gameName") JSONObject gameName) {
        return response("ok");
    }

    @GetMapping("/game/{gameName}/turn/{turnId}")
    public JSONObject getGameTurn(@PathVariable(name = "gameName") String gameName, @PathVariable(name = "turnId") String turnId) {
        return response("ok");
    }

    @PostMapping("/game/{gameName}/turn")
    public JSONObject setGameTurn(@PathVariable(name = "gameName") String gameName, @RequestBody JSONObject actions) {
        return response("ok");
    }

    @GetMapping("/game/{gameName}/possible-actions/{playerName}/{lastTurnId}")
    public JSONObject getPossibleActions(@PathVariable(name = "gameName") String gameName, @PathVariable(name = "playerName") String playerName, @PathVariable(name = "lastTurnId") int turn) {
        return response("ok");
    }

    // Engine functions
    @GetMapping("/engine/")
    public JSONObject getAllEngines() {
        return response("ok");
    }

    @PostMapping("/engine/game-version/{gameVersion}")
    public JSONObject setEngineVersion(@PathVariable(name = "gameVersion") String gameVersion) {
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