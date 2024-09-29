package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Pair;

import java.util.*;

public class ApplicableRuleset implements Cloneable {

    PriorityQueue<Pair<Class<?>, IApplicableRule<?>>> sortedRules;

    public ApplicableRuleset() {
        sortedRules = new PriorityQueue<>(Comparator.comparingInt((p) -> p.right().getPriority().numericValue()));
    }

    public <T> void put(Class<T> c, IApplicableRule<T> rule) {
        sortedRules.add(new Pair<>(c, rule));
    }

    public <T> List<IApplicableRule<T>> get(Class<T> c) {
        return (List<IApplicableRule<T>>) (Object) sortedRules.stream()
                .filter((p) -> p.left().equals(c))
                .map((p) -> p.right()).toList();
    }

    public void applyRules(State state) {
        List<Object> subjects = state.gatherAll();
        Map<Class<?>, List<Object>> subjectByClass = new HashMap<>();

        for (Object subject : subjects) {
            subjectByClass.putIfAbsent(subject.getClass(), new ArrayList<>());
            subjectByClass.get(subject.getClass()).add(subject);
        }

        for (Pair<Class<?>, IApplicableRule<?>> rulePair : sortedRules) {
            Class<?> ruleClass = rulePair.left();
            IApplicableRule<Object> castRule = (IApplicableRule<Object>) rulePair.right();
            for (Class<?> subjectClass : subjectByClass.keySet().stream().filter(ruleClass::isAssignableFrom).toList()) {
                for (Object subject : subjectByClass.get(subjectClass)) {
                    castRule.apply(state, subject);
                }
            }
        }
    }

    @Override
    public ApplicableRuleset clone() {
        try {
            ApplicableRuleset clone = (ApplicableRuleset) super.clone();
            clone.sortedRules.addAll(this.sortedRules);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
