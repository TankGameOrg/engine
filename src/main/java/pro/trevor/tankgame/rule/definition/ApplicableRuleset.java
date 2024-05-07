package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.state.State;

import java.util.*;

public class ApplicableRuleset {

    private final Map<Class<?>, List<IApplicableRule<?>>> rules;

    public ApplicableRuleset() {
        rules = new HashMap<>();
    }

    public <T> void put(Class<T> c, IApplicableRule<T> rule) {
        List<IApplicableRule<?>> list = rules.get(c);
        if (list == null) {
            list = new ArrayList<>();
            list.add(rule);
            rules.put(c, list);
        } else {
            list.add(rule);
        }
    }

    public <T> List<IApplicableRule<T>> get(Class<T> c) {
        if (rules.containsKey(c)) {
            try {
                return (List<IApplicableRule<T>>) ((Object) rules.get(c));
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(0);
    }

    public <T> void applyRules(State state, T subject){
        Class<?> c = subject.getClass();
        for (Class<?> type = c; type != null; type = type.getSuperclass()) {
            for (IApplicableRule<T> rule : (List<IApplicableRule<T>>) (Object) get(type)) {
                rule.apply(state, subject);
            }
        }

    }

    public <T> List<IConditionalRule<T>> applicableConditionalRules(Class<T> c, State state, T subject) {
        List<IConditionalRule<T>> output = new ArrayList<>();
        for (IApplicableRule<T> rule : get(c)) {
            if (rule instanceof IConditionalRule<T> conditional && conditional.canApply(state, subject)) {
                output.add(conditional);
            }
        }
        return output;
    }

    public Set<Class<?>> keySet() {
        return rules.keySet();
    }
}
