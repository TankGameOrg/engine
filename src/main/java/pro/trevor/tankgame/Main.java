package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.version4.Ruleset;
import pro.trevor.tankgame.state.State;

import java.io.File;
import java.nio.file.Files;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("--debug") || args[0].equals("-d"))) {
            DEBUG = true;
            // Demo version 4 rules with game logs
            File initialFile = new File("example/initial-v4.json");
            File movesFile = new File("example/moves-v4.json");
            Api api = new Api(new Ruleset());
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
            Cli.repl(new Ruleset());
        } else {
            System.err.println("Expected 0 or 1 arguments:\n    tankgame <-d|--debug>");
        }
    }
}