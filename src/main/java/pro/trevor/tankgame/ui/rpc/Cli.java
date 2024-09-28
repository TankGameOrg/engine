package pro.trevor.tankgame.ui.rpc;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Cli extends Interface {
    RpcHandler handler;
    Map<String, Method> methodMap;

    public Cli(RpcHandler handler) {
        super(handler);
    }

    public void startRepl() {
        startRepl(System.in, System.out);
    }

    public void startRepl(InputStream input, PrintStream output) {
        Scanner scanner = new Scanner(input);
        while(handler.canProcessRequests()) {
            output.println(handleJson(getJsonObject(scanner)));
        }
    }

    private JSONObject getJsonObject(Scanner input) {
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
        } catch (Exception exception) {
            return new JSONObject();
        }
    }
}
