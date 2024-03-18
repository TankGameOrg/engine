package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.util.ITriConsumer;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class PlayerSelfActionRule<T extends IPlayerElement, U> extends PlayerActionRule <T, T, U>{
    public PlayerSelfActionRule(String name, BiPredicate<T, State> predicate, ITriConsumer<T, Optional<U>, State> consumer) {
        super(name, (t, u, s) -> t == u && predicate.test(t, s), (t, u, v, s) -> consumer.accept(t, v, s));
    }
}
