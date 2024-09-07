package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV4RulesetRegister;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.ui.Cli;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Properties;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length == 2 && (args[0].equals("--debug") || args[0].equals("-d"))) {
            String rulesetName = args[1];
            Optional<Api> maybeApi = RulesetRegistry.createApi(rulesetName);
            if(maybeApi.isEmpty()) {
                System.out.println("The ruleset " + rulesetName + " is not supported");
                System.exit(1);
            }
            File initialFile = new File("example/initial-" + rulesetName + ".json");
            File movesFile = new File("example/moves-" + rulesetName + ".json");
            Api api = maybeApi.get();
            DEBUG = true;
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
                System.exit(1);
            }
        } else if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
            Main.printVersion();
        } else if (args.length == 0) {
            // REPL with the newest default ruleset
            Cli.repl(new DefaultV4RulesetRegister());
        } else {
            System.err.println("Expected 0 or 1 or 2 arguments:\n    tankgame <-d|--debug default-v3|default-v4|-v|--version>");
        }
    }

    /**
     * Print version and git info to stdout as a json object
     */
    private static void printVersion() {
        JSONObject versionInfo = new JSONObject();
        String version = Main.class.getPackage().getImplementationVersion();
        versionInfo.put("version", version);
        versionInfo.put("supported_rulesets", RulesetRegistry.getSupportedRulesetNames());

        String prettyVersion = "Engine " + version;

        try (InputStream in = Main.class.getResourceAsStream("/git.properties")) {
            // If we can't find the git resource skip it
            if(in != null) {
                Properties gitInfo = new Properties();
                gitInfo.load(in);
                versionInfo.put("git_branch", gitInfo.getProperty("git.branch"));
                prettyVersion += " @ " + gitInfo.getProperty("git.commit.id.describe");
            }
        }
        catch(Exception ex) {
            System.err.println("Failed to read git info: " + ex);
        }

        versionInfo.put("pretty_version", prettyVersion);
        System.out.println(versionInfo.toString(4));
    }
}