package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.Result;

import java.util.ArrayList;
import java.util.List;

public class Condition<T extends IPlayerElement> {

    private final Predicate<T>[] predicates;

    public Condition(Predicate<T>... predicates) {
        this.predicates = predicates;
    }

    public Result<List<String>> test(State state, T t, Object... meta) {
        List<String> errors = new ArrayList<>();

        for (Predicate<T> predicate : predicates) {
            Result<String> error = predicate.test(state, t, meta);
            if (error.isError()) {
                errors.add(error.getError());
            }
        }

        if (errors.isEmpty()) {
            return Result.ok();
        } else {
            return Result.error(errors);
        }
    }
}
