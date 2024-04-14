package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.range.TypeRange;

import java.util.*;

public class PlayerRuleset {

    private final Map<Class<?>, List<IPlayerRule<?>>> rules;

    public PlayerRuleset() {
        rules = new HashMap<>();
    }

    public <T extends IPlayerElement> void put(Class<T> t, IPlayerRule<T> rule) {
        List<IPlayerRule<?>> list = rules.get(t);
        if (list == null) {
            list = new ArrayList<>();
            list.add(rule);
            rules.put(t, list);
        } else {
            list.add(rule);
        }
    }

    public <T extends IPlayerElement> List<IPlayerRule<T>> getExact(Class<T> t) {
        if (rules.containsKey(t)) {
            try {
                return (List<IPlayerRule<T>>) ((Object) rules.get(t));
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(0);
    }

    public List<IPlayerRule<?>> get(Class<?> t) {
        if (rules.containsKey(t)) {
            return rules.get(t);
        }
        return new ArrayList<>(0);
    }

    public <T extends IPlayerElement> List<IPlayerRule<T>> applicableRules(Class<T> t, State state, T subject) {
        List<IPlayerRule<T>> output = new ArrayList<>();
        for (IPlayerRule<T> rule : getExact(t)) {
            if (rule instanceof PlayerActionRule<T> conditional && conditional.canApply(state, subject)) {
                output.add(conditional);
            }
        }
        return output;
    }

    public Set<Class<?>> keySet() {
        return rules.keySet();
    }

    public JSONArray toJsonRequirements() {
        JSONArray rulesJson = new JSONArray();
        for (Class<?> key : keySet()) {
            for (IPlayerRule<?> rule : rules.get(key)) {
                JSONObject ruleJson = new JSONObject();
                ruleJson.put("name", rule.name());
                ruleJson.put("subject", key.getSimpleName().toLowerCase());

                JSONArray meta = new JSONArray();
                TypeRange<?>[] ruleParameters = rule.parameters();
                for (TypeRange<?> ruleParameter : ruleParameters) {
                    meta.put(ruleParameter.toJson());
                }
                ruleJson.put("fields", meta);

                rulesJson.put(ruleJson);
            }
        }
        return rulesJson;
    }
}
