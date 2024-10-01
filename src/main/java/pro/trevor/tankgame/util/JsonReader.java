package pro.trevor.tankgame.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonReader {

    public static JSONObject readJson(String file) {
        try (FileReader reader = new FileReader(file)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            bufferedReader.lines().forEachOrdered(sb::append);
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static JSONObject readJson(File file) {
        return readJson(file.getPath());
    }

}
