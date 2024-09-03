package pro.trevor.tankgame;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ReflectionUtil;
import pro.trevor.tankgame.util.RulesetType;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Cli {

    private static final Map<String, IRulesetRegister> RULESETS = new HashMap<>();

    static {
        List<Class<?>> rulesets = ReflectionUtil.allClassesAnnotatedWith(RulesetType.class, "pro.trevor.tankgame");
        for (Class<?> ruleset : rulesets) {
            Class<? extends IRulesetRegister> rulesetClass = (Class<? extends IRulesetRegister>) ruleset;
            RulesetType rulesetType = ruleset.getAnnotation(RulesetType.class);
            try {
                RULESETS.put(rulesetType.name(), rulesetClass.getConstructor().newInstance());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new Error("Error constructing IRuleset: no matching constructor found for class " +
                        rulesetClass, e);
            } catch (InstantiationException e) {
                throw new Error("Error constructing IRuleset: failed to instantiate class " + rulesetClass, e);
            }
        }
    }

    public static List<String> getSupportedRulesetNames() {
        return new ArrayList<String>(RULESETS.keySet());
    }

    public static void repl(IRulesetRegister ruleset) {
        Api api = new Api(ruleset);
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
                        case "display" -> output.println(api.getState().toJson().toString());
                        case "exit" -> {
                            output.println(response("exiting", false));
                            exit = true;
                        }
                        default -> output.println(response("unexpected command: " + command, true));
                    }
                }
                case "version" -> {
                    String version = json.getString("version");
                    IRulesetRegister newRuleset = RULESETS.get(version);
                    if (newRuleset == null) {
                        output.println(response("no such version: " + version, true));
                    } else {
                        api = new Api(newRuleset);
                        output.println(response("switched to version: " + version, false));
                    }
                }
                case "state" -> {
                    try {
                        api.setState(new State(json));
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
                case "possible_actions" -> {
                    try {
                        String subject = json.getString("player");
                        JSONObject actions = new JSONObject();
                        actions.put("error", false);
                        actions.put("type", "possible_actions");
                        actions.put("player", subject);
                        actions.put("actions", PossibleActionsEncoder.encodePossibleActions(
                            api.getPossibleActions(new PlayerRef(subject))));
                        output.println(actions);
                    } catch (Throwable throwable) {
                        output.println(response(throwable.getMessage(), true));
                        throwable.printStackTrace();
                    }
                }

                default -> output.println(response("unexpected type: " + type, true));
            }
        }
    }

    private static JSONObject getJsonObject(Scanner input) {
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
