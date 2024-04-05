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
                output.println(response("input does not have a type", true));
                continue;
            }

            String type = json.getString("type");

            switch (type) {
                case "command" -> {
                    String command = json.getString("command");
                    switch (command) {
                        case "rules" -> output.println(api.getRules().toString());
                        case "actions" -> output.println(api.getPossibleActionsJson().toString());
                        case "display" -> output.println(api.getStateJson().toString());
                        case "exit" -> {
                            output.println(response("exiting successfully", false));
                            exit = true;
                        }
                        default -> output.println(response("unexpected command: " + command, true));
                    }
                }
                case "state" -> {
                    try {
                        api.ingestState(json);
                        output.println(response("state successfully ingested", false));
                    } catch (Throwable throwable) {
                        output.println(response(throwable.getMessage(), true));
                        throwable.printStackTrace();
                    }
                }
                case "action" -> {
                    try {
                        api.ingestAction(json);
                        output.println(response("action successfully ingested", false));
                    } catch (Throwable throwable) {
                        output.println(response(throwable.getMessage(), true));
                        throwable.printStackTrace();
                    }
                }
                default -> output.println(response("unexpected type: " + type, true));
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

    private static JSONObject response(String string, boolean error) {
        JSONObject output = new JSONObject();
        output.put("type", "response");
        output.put("response", string);
        output.put("error", error);
        return output;
    }

}
