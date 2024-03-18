package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;

import java.util.Optional;

public interface IPlayerRule<T, U, V> {

    void apply(State state, T subject, U target, Optional<V> meta);
    boolean canApply(State state, T subject, U target);
    String name();

}
