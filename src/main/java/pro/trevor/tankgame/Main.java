package pro.trevor.tankgame;

import org.json.JSONArray;
import org.json.JSONObject;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV3RulesetRegister;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV4RulesetRegister;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV5RulesetRegister;
import pro.trevor.tankgame.state.State;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length == 2 && (args[0].equals("--debug") || args[0].equals("-d"))) {
            File initialFile;
            File movesFile;
            Api api;
            if (args[1].equals("default-v3")) {
                // Debug the default-v3 ruleset
                initialFile = new File("example/initial-v3.json");
                movesFile = new File("example/moves-v3.json");
                api = new Api(new DefaultV3RulesetRegister());
            } else if (args[1].equals("default-v4")) {
                // Default to debugging default-v4 ruleset
                initialFile = new File("example/initial-v4.json");
                movesFile = new File("example/moves-v4.json");
                api = new Api(new DefaultV4RulesetRegister());
            } else {
                // Default to debugging default-v5 ruleset
                initialFile = new File("example/initial-v5.json");
                movesFile = new File("example/moves-v5.json");
                api = new Api(new DefaultV5RulesetRegister());
            }
            DEBUG = true;
            try {
                String initialString = Files.readString(initialFile.toPath());
                String movesString = Files.readString(movesFile.toPath());

                JSONObject initial = new JSONObject(initialString);
                JSONArray moves = new JSONArray(movesString);

                api.setState(new State(initial));
                System.out.println(api.getState().toJson().toString(2));

                for (int i = 0; i < moves.length(); ++i) {
                    JSONObject action = moves.getJSONObject(i);
                    api.ingestAction(new LogEntry(action));
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
        versionInfo.put("supported_rulesets", Cli.getSupportedRulesetNames());

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