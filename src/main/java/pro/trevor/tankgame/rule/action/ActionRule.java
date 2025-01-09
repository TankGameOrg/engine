package pro.trevor.tankgame.rule.action;

import pro.trevor.tankgame.state.State;

public class ActionRule {

    private final Predicate predicate;
    private final Action action;

    public ActionRule(Predicate predicate,  Action action) {
        this.predicate = predicate;
        this.action = action;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Error apply(State state, LogEntry entry) {
        Error result = predicate.test(state, entry);
        if (result == Error.NONE) {
            return action.apply(state, entry);
        } else {
            return result;
        }
    }

}
