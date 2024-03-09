package rule.impl.enforcer;

import state.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnforcerRuleset {

    private final Map<Class<?>, List<IEnforceable<?>>> rules;

    public EnforcerRuleset() {
        rules = new HashMap<>();
    }

    public <T> void put(Class<T> c, IEnforceable<T> rule) {
        List<IEnforceable<?>> list = rules.get(c);
        if (list == null) {
            list = new ArrayList<>();
            list.add(rule);
            rules.put(c, list);
        } else {
            list.add(rule);
        }
    }

    public <T> List<IEnforceable<T>> get(Class<T> c) {
        if (rules.containsKey(c)) {
            try {
                return (List<IEnforceable<T>>) ((Object) rules.get(c));
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(0);
    }

    public <T> void enforceRules(State state, T subject){
        Class<?> c = subject.getClass();
        for (IEnforceable<T> rule : (List<IEnforceable<T>>) (Object) get(c)) {
            rule.enforce(state, subject);
        }
    }
}
