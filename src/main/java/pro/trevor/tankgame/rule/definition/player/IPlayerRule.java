package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;

public interface IPlayerRule {

    void apply(State state, PlayerRef subject, Object... meta);
    boolean canApply(State state, PlayerRef subject, Object... meta);

    String name();
    TypeRange<?>[] parameters();

}
