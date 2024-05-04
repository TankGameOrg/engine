package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.function.IVarTriConsumer;
import pro.trevor.tankgame.util.function.IVarTriPredicate;
import pro.trevor.tankgame.util.range.TypeRange;

import java.util.function.Function;

public class TimedPlayerActionRule<T extends ICooldownPlayerElement> extends PlayerActionRule<T> {

    // Returns the cooldown in milliseconds for the function based on the state
    private final Function<State, Long> cooldownFunction;

    public TimedPlayerActionRule(String name, Function<State, Long> cooldownFunction, IVarTriPredicate<State, T, Object> predicate, IVarTriConsumer<State, T, Object> consumer, TypeRange<?>... parameters) {
        super(name, predicate, consumer, parameters);
        this.cooldownFunction = cooldownFunction;
    }

    @Override
    public void apply(State state, T subject, Object... meta) {
        long cooldown = cooldownFunction.apply(state);
        long elapsed = System.currentTimeMillis() - subject.getLastUsage(name);
        if (elapsed >= cooldown) {
            super.apply(state, subject, meta);
            subject.setLastUsage(name, System.currentTimeMillis());
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);
            error.put("cooldown", cooldown);
            error.put("elapsed", elapsed);

            if (subject instanceof IJsonObject subjectJson) {
                error.put("subject", subjectJson.toJson());
            } else {
                error.put("subject", subject.getPlayer());
            }

            System.err.println(error.toString(2));
            throw new Error(String.format("Rule %s has cooldown of %dms but only waited %dms", name, cooldown, elapsed));
        }
    }

    @Override
    public boolean canApply(State state, T subject, Object... meta) {
        long cooldown = cooldownFunction.apply(state);
        long elapsed = System.currentTimeMillis() - subject.getLastUsage(name);
        return elapsed >= cooldown && super.canApply(state, subject, meta);
    }

}
