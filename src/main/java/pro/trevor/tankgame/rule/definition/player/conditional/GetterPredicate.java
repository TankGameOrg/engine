package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.Optional;
import java.util.function.BiFunction;

public class GetterPredicate<T extends AttributeContainer> extends RulePredicate {
    public GetterPredicate(BiFunction<State, PlayerRef, Optional<T>> getter, IVarTriPredicate<State, T, Object> predicate, String message) {
        super((state, player, n) -> {
            Optional<T> optionalT = getter.apply(state, player);
            return optionalT.isPresent() && predicate.test(state, optionalT.get(), n);
        }, message);
    }
}
