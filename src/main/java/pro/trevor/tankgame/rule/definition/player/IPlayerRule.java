package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;

public interface IPlayerRule<T> {

    void apply(State state, T subject, Object... meta);
    boolean canApply(State state, T subject, Object... meta);

    String name();
    Class<?>[] paramTypes();
    String[] paramNames();

}
