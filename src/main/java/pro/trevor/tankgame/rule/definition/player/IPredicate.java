package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;

public interface IPredicate<T extends IPlayerElement> {
    Result<String> test(State state, T t, Object... meta);
}
