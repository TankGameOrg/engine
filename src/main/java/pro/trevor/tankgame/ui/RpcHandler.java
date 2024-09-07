package pro.trevor.tankgame.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.RulesetRegistry;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class RpcHandler implements IRpcHandler {
    private boolean isRunning = true;
    private Map<String, Api> apis = new HashMap<>();

    public boolean canProcessRequests() {
        return isRunning;
    }

    private Api getApi(JSONObject request) {
        String instance = "default";
        if(request.has("instance")) {
            instance = request.getString("instance");
        }

        if(!apis.containsKey(instance)) {
            throw new Error("No such instance " + instance + ". You need to run the create_instance command.");
        }

        return apis.get(instance);
    }

    @RpcMethod(type = "command")
    public JSONObject command(JSONObject request) {
        String command = request.getString("command");
        switch (command) {
            case "display" -> {
                return getApi(request).getState().toJson();
            }
            case "exit" -> {
                isRunning = false;
                return response("exiting");
            }
            default -> {
                throw new Error("Unexpected command: " + command);
            }
        }
    }

    @RpcMethod(type = "version")
    public JSONObject version(JSONObject request) {
        System.err.println("Warning the version command is deprectated and will be removed.  Use create_instance instead");
        request.put("ruleset", request.getString("version"));
        return createInstance(request);
    }

    @RpcMethod(type = "create_instance")
    public JSONObject createInstance(JSONObject request) {
        String rulesetName = request.getString("ruleset");
        Optional<Api> newRuleset = RulesetRegistry.createApi(rulesetName);
        if (newRuleset.isEmpty()) {
            throw new Error("no such ruleset: " + rulesetName);
        } else {
            String instance = "default";
            if(request.has("instance")) {
                instance = request.getString("instance");
            }

            apis.put(instance, newRuleset.get());
            return response("Created instance " + instance + " with rulset " + rulesetName);
        }
    }

    @RpcMethod(type = "destroy_instance")
    public JSONObject destroyInstance(JSONObject request) {
        String instance = "default";
        if(request.has("instance")) {
            instance = request.getString("instance");
        }

        apis.remove(instance);
        return response("Destroyed instance " + instance);
    }

    @RpcMethod(type = "state")
    public JSONObject setState(JSONObject request) {
        getApi(request).setState(new State(request));
        return response("state successfully ingested");
    }

    @RpcMethod(type = "action")
    public JSONObject ingestAction(JSONObject request) {
        getApi(request).ingestAction(new LogEntry(request));
        return response("action successfully ingested");
    }

    @RpcMethod(type = "can_ingest_action")
    public JSONObject canIngestAction(JSONObject request) {
        JSONArray errors = new JSONArray(
            getApi(request).canIngestAction(new LogEntry(request)).stream()
                .map((error) -> PlayerRuleErrorEncoder.encode(error))
                .toList()
        );

        JSONObject response = new JSONObject();
        response.put("errors", errors);
        return response;
    }

    @RpcMethod(type = "possible_actions")
    public JSONObject getPossibleActions(JSONObject request) {
        String subject = request.getString("player");
        JSONObject actions = new JSONObject();
        actions.put("error", false);
        actions.put("type", "possible_actions");
        actions.put("player", subject);
        actions.put("actions", PossibleActionsEncoder.encodePossibleActions(
            getApi(request).getPossibleActions(new PlayerRef(subject))));
        return actions;
    }

    private JSONObject response(String message) {
        return new JSONObject(Map.of("response", message));
    }
}
