package pro.trevor.tankgame.rule.action;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.Player;

public interface Precondition {
    Error test(State state, Player player);

}
