package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.DuoClass;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerRuleset {

    private final Map<DuoClass<?, ?>, List<IPlayerRule<?, ?, ?>>> rules;

    public PlayerRuleset() {
        rules = new HashMap<>();
    }

    public <T extends IPlayerElement, U, V> void put(Class<T> t, Class<U> u, Class<V> v, IPlayerRule<T, U, V> rule) {
        DuoClass<T, U> c = new DuoClass<>(t, u);
        List<IPlayerRule<?, ?, ?>> list = rules.get(c);
        if (list == null) {
            list = new ArrayList<>();
            list.add(rule);
            rules.put(c, list);
        } else {
            list.add(rule);
        }
    }

    public <T extends IPlayerElement, U> void putSelfRule(Class<T> t, Class<U> u, IPlayerRule<T, T, U> rule) {
        put(t, t, u, rule);
    }

    public <T extends IPlayerElement, U> List<IPlayerRule<T, U, ?>> getExact(Class<T> t, Class<U> u) {
        DuoClass<T, U> c = new DuoClass<>(t, u);
        if (rules.containsKey(c)) {
            try {
                return (List<IPlayerRule<T, U, ?>>) ((Object) rules.get(c));
            } catch (Exception ignored) {
            }
        }
        return new ArrayList<>(0);
    }

    public List<IPlayerRule<?, ?, ?>> get(Class<?> t, Class<?> u) {
        DuoClass<?, ?> c = new DuoClass<>(t, u);
        if (rules.containsKey(c)) {
            return rules.get(c);
        }
        return new ArrayList<>(0);
    }

    public <T extends IPlayerElement> List<IPlayerRule<T, ? ,?>> getExactRulesForSubject(Class<T> t) {
        return rules.keySet().stream().filter(k -> k.getLeftClass().equals(t))
                .map(k -> (List<IPlayerRule<T, ?, ?>>) ((Object)rules.get(k)))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public <T extends IPlayerElement> List<IPlayerRule<?, T, ?>> getExactRulesForTarget(Class<T> t) {
        return rules.keySet().stream().filter(k -> k.getRightClass().equals(t))
                .map(k -> (List<IPlayerRule<?, T, ?>>) ((Object)rules.get(k)))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public <T extends IPlayerElement, U> List<IPlayerRule<T, U, ?>> applicableRules(Class<T> t, Class<U> u, State state, T subject, U target) {
        List<IPlayerRule<T, U, ?>> output = new ArrayList<>();
        for (IPlayerRule<T, U, ?> rule : getExact(t, u)) {
            if (rule instanceof PlayerActionRule<T, U, ?> conditional && conditional.canApply(state, subject, target)) {
                output.add(conditional);
            }
        }
        return output;
    }

    public Set<DuoClass<?, ?>> keySet() {
        return rules.keySet();
    }
}
