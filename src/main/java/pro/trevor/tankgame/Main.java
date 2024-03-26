package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.version3.Api;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        System.out.println(Arrays.toString(args));
        if (args.length == 0) {
            // Only read from stdin (repl)
        } else if (args.length == 1) {
            // read from specified file as initial state
        } else {
            System.err.println("Expected 0 or 1 arguments:\n    tankgame <path/to/initial.json>");
        }

        // Demo version 3 rules with v3 logs
        File initialFile = new File("example/initial.json");
        File movesFile = new File("example/moves.json");
        IApi api = new Api();
        try {
            String initialString = Files.readString(initialFile.toPath());
            String movesString = Files.readString(movesFile.toPath());

            JSONObject initial = new JSONObject(initialString);
            JSONArray moves = new JSONArray(movesString);

            api.ingestState(initial);
            for (int i = 0; i < moves.length(); ++i) {
                JSONObject action = moves.getJSONObject(i);
                api.ingestAction(action);

            }
        } catch (Throwable e) {
            api.printStateJson(true);
            api.printPossibleMovesJson(true);
            e.printStackTrace();
        }
    }
}