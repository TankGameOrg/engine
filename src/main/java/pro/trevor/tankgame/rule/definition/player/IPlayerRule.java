package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.IJsonObject;

import java.util.Optional;

public interface IPlayerRule<T, U> {

    void apply(State state, T subject, U target, Object... meta);
    boolean canApply(State state, T subject, U target, Object... meta);

    String name();
    Class<?>[] optional();

}
