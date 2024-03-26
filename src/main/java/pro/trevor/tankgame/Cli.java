package pro.trevor.tankgame;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Cli {

    public static void repl(IApi api) {
        PrintStream output = System.out;
        InputStream input = System.in;
        Scanner scanner = new Scanner(input);
        boolean exit = false;
        JSONObject json;
        while (!exit) {
            json = getJsonObject(scanner);
            if (!json.has("type")) {
                System.err.println("JSON input does not have a type, try again");
                continue;
            }

            String type = json.getString("type");

            if (type.equals("command")) {
                String command = json.getString("command");
                switch (command) {
                    case "actions" -> output.println(api.getPossibleActionsJson().toString(2));
                    case "display" -> output.println(api.getStateJson().toString(2));
                    case "exit" -> exit = true;
                    default -> System.err.printf("Unexpected command: `%s`\n", command);
                }
            } else if (type.equals("state")) {
                try {
                    api.ingestState(json);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else if (type.equals("action")) {
                try {
                    api.ingestAction(json);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                System.err.printf("Unexpected type `%s`", type);
            }
        }
    }

    public static JSONObject getJsonObject(Scanner input) {
        Pattern oldPattern = input.delimiter();
        StringBuilder sb = new StringBuilder();
        input.useDelimiter("");
        int count = 0;
        boolean seeking = true;
        boolean inQuote = false;
        do {
            char current = input.next().charAt(0);
            sb.append(current);
            if (current == '{' && !inQuote) {
                count = count + 1;
                seeking = false;
            } else if (current == '}' && !inQuote) {
                count = count - 1;
            } else if (current == '\"')  {
                inQuote = !inQuote;
            }

        } while (count > 0 || seeking);

        input.useDelimiter(oldPattern);

        try {
            return new JSONObject(sb.toString());
        } catch (Exception e) {
            return new JSONObject();
        }
    }

}
