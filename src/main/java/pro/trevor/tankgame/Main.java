package pro.trevor.tankgame;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static boolean DEBUG = false;

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("-v") || args[0].equals("--version"))) {
            Main.printVersion();
        } else if (args.length == 0) {
            // TODO
            System.err.println("CLI is not yet implemented");
        } else {
            System.err.println("Expected 0 or 1 arguments:\n    tankgame <-v|--version>");
        }
    }

    /**
     * Print version and git info to stdout as a json object
     */
    private static void printVersion() {
        JSONObject versionInfo = new JSONObject();
        String version = Main.class.getPackage().getImplementationVersion();
        versionInfo.put("version", version);

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
        catch(Exception e) {
            System.err.println("Failed to read git info: " + e.getMessage());
        }

        versionInfo.put("pretty_version", prettyVersion);
        System.out.println(versionInfo.toString(4));
    }
}