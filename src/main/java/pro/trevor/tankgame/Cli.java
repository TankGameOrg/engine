package pro.trevor.tankgame;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.IApi;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cli {

    public static void repl(IApi api) {
        PrintStream output = System.out;
        InputStream input = System.in;
        Scanner scanner = new Scanner(input);
        boolean exit = false;
        while (!exit) {
            String command = scanner.next();
            switch (command) {
                case "actions" -> output.println(api.getPossibleActionsJson());
                case "display" -> output.println(api.getStateJson());
                case "state" -> api.ingestState(getJsonObject(scanner));
                case "action" -> api.ingestAction(getJsonObject(scanner));
                case "exit" -> exit = true;
                default -> System.err.printf("Unexpected command: `%s`\n", command);
            }
        }
    }

    private static final Pattern lbracket = Pattern.compile("[^{]*\\{");
    private static final Pattern rbracket = Pattern.compile("[^}]*}");

    public static JSONObject getJsonObject(Scanner input) {
        Pattern oldPattern = input.delimiter();
        StringBuilder sb = new StringBuilder();
        input.useDelimiter("}");

        // We could do this much more efficiently if we had access to the input stream within the buffer.
        // Seek while appending to string; `{` is +1, `}` is -1. Continue to read until the sum is 0.
        // We cannot do this with the current implementation since Scanner reads more characters than necessary and puts
        // them into a buffer; this means we will not necessarily get the first `{` when reading from the input stream.
        // We might be able to solve this by writing our own scanner with the byte-by-byte access we need.
        do {
            String current = input.next();
            sb.append(current).append('}');

        } while (!isBracketComplete(sb));

        input.useDelimiter("");
        input.next(); // skip the `}` remaining in the buffer

        input.useDelimiter(oldPattern);

        return new JSONObject(sb.toString());
    }

    private static boolean isBracketComplete(StringBuilder string) {
        int lbrack = 0;
        int rbrack = 0;
        Matcher lBracketMatcher = lbracket.matcher(string);
        Matcher rBracketMatcher = rbracket.matcher(string);

        while (lBracketMatcher.find()) {
            ++lbrack;
        }
        while (rBracketMatcher.find()) {
            ++rbrack;
        }

        return lbrack <= rbrack;
    }

}
