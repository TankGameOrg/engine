package rule.definition.player;

import rule.type.IPlayerElement;
import state.State;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class PlayerSelfActionRule<T extends IPlayerElement> extends PlayerActionRule <T, T>{
    public PlayerSelfActionRule(String name, BiPredicate<T, State> predicate, BiConsumer<T, State> consumer) {
        super(name, (x, y, z) -> x == y && predicate.test(y, z), (x, y, z) -> consumer.accept(y, z));
    }
}
