package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.Pair;

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

    public Optional<Pair<Class<?>, IPlayerRule<?>>> getByName(String name) {
        for (Class<?> ruleClass : rules.keySet()) {
            List<IPlayerRule<?>> list = rules.get(ruleClass);
            for (IPlayerRule<?> rule : list) {
                if (rule.name().equals(name)) {
                    return Optional.of(new Pair<>(ruleClass, rule));
                }
            }
        }

        return Optional.empty();
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
