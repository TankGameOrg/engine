package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class GetterPredicate<T extends IPlayerElement> extends RulePredicate {

    public GetterPredicate(BiFunction<State, PlayerRef, T> getter, BiFunction<State, T, Result<String>> predicate) {
        super((s, p) -> predicate.apply(s, getter.apply(s, p)));
    }

    public GetterPredicate(BiFunction<State, PlayerRef, T> getter, BiPredicate<State, T> predicate, String message) {
        super((s, p) -> predicate.test(s, getter.apply(s, p)), message);
    }
}
