package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;
import pro.trevor.tankgame.rule.impl.version3.Api;

import java.io.File;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {
        File initialFile = new File("example/initial.json");
        File movesFile = new File("example/moves.json");
        try {
            String initialString = Files.readString(initialFile.toPath());
            String movesString = Files.readString(movesFile.toPath());

            JSONObject initial = new JSONObject(initialString);
            JSONArray moves = new JSONArray(movesString);

            IApi api = new Api();
            api.ingestState(initial);
            for (int i = 0; i < moves.length(); ++i) {
                JSONObject action = moves.getJSONObject(i);
                api.ingestAction(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}