package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.*;

public class PlayerRuleset {

    private final List<IPlayerRule> rules;

    public PlayerRuleset() {
        rules = new ArrayList<>();
    }

    public void add(IPlayerRule rule) {
        rules.add(rule);
    }

    public Optional<IPlayerRule> getByName(String name) {
        for (IPlayerRule rule : rules) {
            if (rule.name().equals(name)) {
                return Optional.of(rule);
            }
        }

        return Optional.empty();
    }

    public List<IPlayerRule> getAllRules() {
        return rules;
    }

    public JSONArray toJsonRequirements() {
        JSONArray rulesJson = new JSONArray();
        for (IPlayerRule rule : rules) {
            JSONObject ruleJson = new JSONObject();
            ruleJson.put("name", rule.name());
            ruleJson.put("subject", Codec.typeFromClass(PlayerRef.class));

            JSONArray meta = new JSONArray();
            TypeRange<?>[] ruleParameters = rule.parameters();
            for (TypeRange<?> ruleParameter : ruleParameters) {
                meta.put(ruleParameter.toJson());
            }
            ruleJson.put("fields", meta);

            rulesJson.put(ruleJson);
        }
        return rulesJson;
    }
}
