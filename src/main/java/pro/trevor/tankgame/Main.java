package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.version3.Api;

import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

public class Main {

    public static final boolean DEBUG = false;

    public static void main(String[] args) {

        if (DEBUG) {
            // Demo version 3 rules with game logs
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
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else if (args.length == 0) {
            Cli.repl(new Api());
        } else if (args.length == 1) {
            IApi api = new Api();
            File initialFile = new File(args[0]);
            try {
                String initialString = Files.readString(initialFile.toPath());
                JSONObject initialState = new JSONObject(initialString);
                api.ingestState(initialState);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                System.exit(1);
            }
            Cli.repl(api);
        } else {
            System.err.println("Expected 0 or 1 arguments:\n    tankgame <path/to/initial.json>");
        }


    }
}