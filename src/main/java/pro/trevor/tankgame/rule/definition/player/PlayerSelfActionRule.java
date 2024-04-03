package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.IVarTriConsumer;
import pro.trevor.tankgame.util.IVarTriPredicate;

public class PlayerSelfActionRule<T extends IPlayerElement, U> extends PlayerActionRule <T, T>{
    public PlayerSelfActionRule(String name, IVarTriPredicate<State, T, Object> predicate, IVarTriConsumer<State, T, Object> consumer, Class<?>... optional) {
        super(name, (s, t, u, v) -> t == u && predicate.test(s, t, v), (s, t, u, v) -> consumer.accept(s, t, v), optional);
    }
}
