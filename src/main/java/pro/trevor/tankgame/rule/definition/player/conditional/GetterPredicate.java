package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriFunction;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.function.BiFunction;

public class GetterPredicate<T extends AttributeObject> extends RulePredicate {

    public GetterPredicate(BiFunction<State, PlayerRef, T> getter, IVarTriFunction<State, T, Object, Result<String>> predicate) {
        super((s, p, n) -> predicate.accept(s, getter.apply(s, p), n));
    }

    public GetterPredicate(BiFunction<State, PlayerRef, T> getter, IVarTriPredicate<State, T, Object> predicate, String message) {
        super((s, p, n) -> predicate.test(s, getter.apply(s, p), n), message);
    }
}
