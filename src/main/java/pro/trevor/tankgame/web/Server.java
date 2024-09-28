package pro.trevor.tankgame.web;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
public class Server {

    @GetMapping("/game/{gameId}")
    public String game(@PathVariable(name = "gameId") String gameId) {
        return "Serving " + gameId + "!\n";
    }

    @PutMapping("/game/{gameId}")
    public String game(@PathVariable(name = "gameId") String gameId, @RequestBody String move) {
        System.out.println(new JSONObject(move).toString(2));
        return "ok";
    }
}