package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class AttributePredicate<T extends AttributeObject> extends GetterPredicate<T> {

    public AttributePredicate(BiFunction<State, PlayerRef, T> getter, Function<T, Result<String>> predicate) {
        super(getter, (s, t, n) -> predicate.apply(t));
    }

    public AttributePredicate(BiFunction<State, PlayerRef, T> getter, Predicate<T> predicate, String error) {
        super(getter, (s, t, n) -> predicate.test(t) ? Result.ok() : Result.error(error));
    }
}
