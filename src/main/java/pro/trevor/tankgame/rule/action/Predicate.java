package pro.trevor.tankgame.rule.action;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Predicate {

    private final List<Precondition> preconditions;
    private final List<Condition> conditions;

    public Predicate() {
        this.preconditions = new ArrayList<>();
        this.conditions = new ArrayList<>();
    }

    public Predicate(List<Precondition> preconditions, List<Condition> conditions) {
        this.preconditions = new ArrayList<>(preconditions);
        this.conditions = new ArrayList<>(conditions);
    }

    public Predicate with(Precondition... preconditions) {
        this.preconditions.addAll(Arrays.asList(preconditions));
        return this;
    }

    public Predicate with(Condition... conditions) {
        this.conditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public List<Error> test(State state, Player player) {
        List<Error> errors = new ArrayList<>();
        for (Precondition precondition : preconditions) {
            Error result  = precondition.test(state, player);
            if (result != Error.NONE) {
                errors.add(result);
            }
        }
        return errors;
    }

    public Error test(State state, LogEntry entry) {
        for (Condition condition : conditions) {
            Error result  = condition.test(state, entry);
            if (result != Error.NONE) {
                return result;
            }
        }
        return Error.NONE;
    }

}
