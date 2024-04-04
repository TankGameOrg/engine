package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;

public interface IPlayerRule<T, U> {

    void apply(State state, T subject, U target, Object... meta);
    boolean canApply(State state, T subject, U target, Object... meta);

    String name();
    Class<?>[] paramTypes();
    String[] paramNames();

}
