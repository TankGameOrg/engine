package pro.trevor.tankgame.rule.definition.enforcer;

import pro.trevor.tankgame.state.State;

import java.util.*;

public class EnforcerRuleset implements Cloneable {

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

    public void enforceRules(State state) {
        List<Object> subjects = state.gatherAll();
        Map<Class<?>, List<Object>> subjectByClass = new HashMap<>();

        for (Object subject : subjects) {
            subjectByClass.putIfAbsent(subject.getClass(), new ArrayList<>());
            subjectByClass.get(subject.getClass()).add(subject);
        }

        for (Map.Entry<Class<?>, List<IEnforceable<?>>> entry : rules.entrySet()) {
            Class<?> ruleClass = entry.getKey();
            for (List<Object> subjectList : subjectByClass.entrySet().stream()
                    .filter((e) -> ruleClass.isAssignableFrom(e.getKey()))
                    .map(Map.Entry::getValue)
                    .toList()) {
                for (Object subject : subjectList) {
                    for (IEnforceable<?> rule : entry.getValue()) {
                        IEnforceable<Object> castRule = (IEnforceable<Object>) rule;
                        castRule.enforce(state, subject);
                    }
                }
            }
        }
    }

    @Override
    public EnforcerRuleset clone() {
        try {
            EnforcerRuleset clone = (EnforcerRuleset) super.clone();
            clone.rules.putAll(this.rules);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
