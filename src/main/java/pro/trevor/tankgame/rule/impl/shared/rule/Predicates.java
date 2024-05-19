package pro.trevor.tankgame.rule.impl.shared.rule;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.GenericElement;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

public class Predicates {

    public static IVarTriPredicate<State, GenericElement, Object> MinimumResourcePredicate(Attribute<Integer> attribute,
            int minAmount) {
        return (state, element, n) -> {
            return attribute.from(element).orElse(0) >= minAmount;
        };
    }

    public static IVarTriPredicate<State, GenericElement, Object> MaximumResourcePredicate(Attribute<Integer> attribute,
            int maxAmount) {
        return (state, element, n) -> {
            return attribute.from(element).orElse(0) <= maxAmount;
        };
    }

    public static IVarTriPredicate<State, GenericElement, Object> ExactResourcePredicate(Attribute<Integer> attribute,
            int amount) {
        return (state, element, n) -> {
            return attribute.from(element).orElse(0) == amount;
        };
    }

}
