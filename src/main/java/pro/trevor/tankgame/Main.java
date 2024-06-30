package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.util.ApiRegistry;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.version3.ApiV3;
import pro.trevor.tankgame.rule.impl.version4.ApiV4;
import pro.trevor.tankgame.state.State;

import java.io.File;
import java.nio.file.Files;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        ApiRegistry.putApi("3", ApiV3.class);
        ApiRegistry.putApi("4", ApiV4.class);

        if (args.length == 1 && (args[0].equals("--debug") || args[0].equals("-d"))) {
            DEBUG = true;
            // Demo version 3 rules with game logs
            File initialFile = new File("example/initial.json");
            File movesFile = new File("example/moves.json");
            IApi api = new ApiV3();
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
            Cli.repl(new ApiV3());
        } else {
            System.err.println("Expected 0 or 1 arguments:\n    tankgame <-d|--debug>");
        }
    }
}