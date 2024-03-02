package rule.impl.player;

import rule.type.IPlayerElement;
import state.State;
import util.DuoClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerRuleset {

    private final Map<DuoClass<?, ?>, List<IPlayerRule<?, ?>>> rules;

    public PlayerRuleset() {
        rules = new HashMap<>();
    }

    public <T extends IPlayerElement, U> void put(Class<T> t, Class<U> u, IPlayerRule<T, U> rule) {
        DuoClass<T, U> c = new DuoClass<>(t, u);
        List<IPlayerRule<?, ?>> list = rules.get(c);
        if (list == null) {
            list = new ArrayList<>();
            list.add(rule);
            rules.put(c, list);
        } else {
            list.add(rule);
        }
    }

    public <T extends IPlayerElement> void putSelfRule(Class<T> t, IPlayerRule<T, T> rule) {
        put(t, t, rule);
    }

    public <T extends IPlayerElement, U> List<IPlayerRule<T, U>> get(Class<T> t, Class<U> u) {
        DuoClass<T, U> c = new DuoClass<>(t, u);
        if (rules.containsKey(c)) {
            try {
                return (List<IPlayerRule<T, U>>) ((Object) rules.get(c));
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(0);
    }

    public <T extends IPlayerElement> List<IPlayerRule<T, T>> getSelfRules(Class<T> t) {
        return get(t, t);
    }

    public <T extends IPlayerElement> List<IPlayerRule<T, ?>> getAllRulesForSubject(Class<T> t) {
        return rules.keySet().stream().filter(k -> k.getLeftClass().equals(t))
                .map(k -> (IPlayerRule<T, ?>) rules.get(k))
                .collect(Collectors.toList());
    }

    public <T extends IPlayerElement> List<IPlayerRule<?, T>> getAllRulesForTarget(Class<T> t) {
        return rules.keySet().stream().filter(k -> k.getRightClass().equals(t))
                .map(k -> (IPlayerRule<?, T>) rules.get(k))
                .collect(Collectors.toList());
    }

    public <T extends IPlayerElement, U> List<IPlayerRule<T, U>> applicableRules(Class<T> t, Class<U> u, State state, T subject, U target) {
        List<IPlayerRule<T, U>> output = new ArrayList<>();
        for (IPlayerRule<T, U> rule : get(t, u)) {
            if (rule instanceof PlayerActionRule<T, U> conditional && conditional.canApply(state, subject, target)) {
                output.add(conditional);
            }
        }
        return output;
    }

    public <T extends IPlayerElement> List<IPlayerRule<T, T>> applicableSelfRules(Class<T> t, State state, T target) {
        return applicableRules(t, t, state, target, target);
    }

}
