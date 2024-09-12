package pro.trevor.tankgame.ui.rpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import pro.trevor.tankgame.Api;
import pro.trevor.tankgame.PlayerRuleErrorEncoder;
import pro.trevor.tankgame.PossibleActionsEncoder;
import pro.trevor.tankgame.RulesetRegistry;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;


public class RpcHandler {
    private boolean isRunning = true;
    private Map<String, Api> apis = new HashMap<>();

    public boolean canProcessRequests() {
        return isRunning;
    }

    private Api getApi(JSONObject request) {
        if(!request.has("instance")) {
            throw new Error("You must specify an instance to call");
        }

        String instance = request.getString("instance");

        if(!apis.containsKey(instance)) {
            throw new Error("No such instance " + instance + ". You need to run the create_instance command.");
        }

        return apis.get(instance);
    }

    @RpcMethod
    public JSONObject exit(JSONObject request) {
        isRunning = false;
        return response("exiting");
    }

    @RpcMethod
    public JSONObject getState(JSONObject request) {
        return getApi(request).getState().toJson();
    }

    @RpcMethod
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

    @RpcMethod
    public JSONObject destroyInstance(JSONObject request) {
        String instance = "default";
        if(request.has("instance")) {
            instance = request.getString("instance");
        }

        apis.remove(instance);
        return response("Destroyed instance " + instance);
    }

    @RpcMethod
    public JSONObject setState(JSONObject request) {
        getApi(request).setState(new State(request));
        return response("state successfully ingested");
    }

    @RpcMethod
    public JSONObject ingestAction(JSONObject request) {
        getApi(request).ingestAction(new LogEntry(request));
        return response("action successfully ingested");
    }

    @RpcMethod
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

    @RpcMethod
    public JSONObject canInjestAction(JSONObject request) {
        JSONArray errors = new JSONArray(
            getApi(request).canIngestAction(new LogEntry(request)).stream()
                .map((error) -> PlayerRuleErrorEncoder.encode(error))
                .toList()
        );

        JSONObject response = new JSONObject();
        response.put("errors", errors);
        response.put("error", false);
        return response;
    }

    private JSONObject response(String message) {
        return new JSONObject(Map.of("response", message));
    }
}
