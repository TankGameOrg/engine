package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.State;

import java.io.File;
import java.nio.file.Files;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length == 2 && (args[0].equals("--debug") || args[0].equals("-d"))) {
            File initialFile;
            File movesFile;
            Api api;
            if (args[1].equals("default-v3")) {
                // Debug the default-v3 ruleset
                initialFile = new File("example/initial-v3.json");
                movesFile = new File("example/moves-v3.json");
                api = new Api(new pro.trevor.tankgame.rule.impl.version3.Ruleset());
            } else {
                // Default to debugging default-v4 ruleset
                initialFile = new File("example/initial-v4.json");
                movesFile = new File("example/moves-v4.json");
                api = new Api(new pro.trevor.tankgame.rule.impl.version4.Ruleset());
            }
            DEBUG = true;
            try {
                String initialString = Files.readString(initialFile.toPath());
                String movesString = Files.readString(movesFile.toPath());

                System.out.println(api.getRules().toString(2));

                JSONObject initial = new JSONObject(initialString);
                JSONArray moves = new JSONArray(movesString);

                api.setState(new State(initial));
                System.out.println(api.getState().toJson().toString(2));

                for (int i = 0; i < moves.length(); ++i) {
                    JSONObject action = moves.getJSONObject(i);
                    api.ingestAction(action);
                }
                System.out.println(api.getState().toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else if (args.length == 0) {
            // REPL with the newest default ruleset
            Cli.repl(new pro.trevor.tankgame.rule.impl.version4.Ruleset());
        } else {
            System.err.println("Expected 0 or 2 arguments:\n    tankgame <-d|--debug default-v3|default-v4>");
        }
    }
}