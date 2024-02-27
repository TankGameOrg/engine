package rule.impl.enforcer;

import state.board.IElement;

import java.util.function.*;

public class MinimumEnforcer<T extends IElement, U extends Comparable<U>> extends PredicateEnforcer<T, U> {

    public MinimumEnforcer(Function<T, U> getter, BiConsumer<T, U> setter, U bound) {
        super((x) -> getter.apply(x).compareTo(bound) > 0, setter, bound);
    }
}
