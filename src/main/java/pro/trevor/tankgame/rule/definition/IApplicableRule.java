package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.state.State;

public interface IApplicableRule<T> {
    void apply(State state, T subject);
    Priority getPriority();

    class Comparator implements java.util.Comparator<IApplicableRule<?>> {
        @Override
        public int compare(IApplicableRule<?> o1, IApplicableRule<?> o2) {
            return o2.getPriority().numericValue() - o1.getPriority().numericValue();
        }
    }
}
