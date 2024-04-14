package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.range.TypeRange;

public interface IPlayerRule<T> {

    void apply(State state, T subject, Object... meta);
    boolean canApply(State state, T subject, Object... meta);

    String name();
    TypeRange<?>[] parameters();

}
