package pro.trevor.tankgame.rule.definition;

import pro.trevor.tankgame.state.State;

public interface IConditionalRule<T> extends IApplicableRule<T> {

    boolean canApply(State state, T subject);
}
