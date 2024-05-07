package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.rule.type.ICooldownPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.function.IVarTriConsumer;
import pro.trevor.tankgame.util.function.IVarTriPredicate;
import pro.trevor.tankgame.util.range.TypeRange;

import java.util.Arrays;
import java.util.function.Function;

public class TimedPlayerActionRule<T extends ICooldownPlayerElement> extends PlayerActionRule<T> {

    // Returns the cooldown for the function based on the state
    private final Function<State, Long> cooldownFunction;

    public TimedPlayerActionRule(String name, Function<State, Long> cooldownFunction, IVarTriPredicate<State, T, Object> predicate, IVarTriConsumer<State, T, Object> consumer, TypeRange<?>... parameters) {
        super(name, predicate, consumer, parameters);
        this.cooldownFunction = cooldownFunction;
    }

    public TimedPlayerActionRule(PlayerActionRule<T> rule, Function<State, Long> cooldownFunction) {
        super(rule.name, rule.predicate, rule.consumer, rule.parameters);
        this.cooldownFunction = cooldownFunction;
    }

    @Override
    public void apply(State state, T subject, Object... meta) {
        long timeOfAction = (long) meta[0];
        long cooldown = cooldownFunction.apply(state);
        long elapsed = timeOfAction - subject.getLastUsage(name);
        if (elapsed >= cooldown) {
            Object[] appliedMeta = Arrays.copyOfRange(meta, 1, meta.length);
            if (super.canApply(state, subject, appliedMeta)) {
                consumer.accept(state, subject, appliedMeta);
            } else {
                JSONObject error = new JSONObject();
                error.put("error", true);
                error.put("rule", name);

                if (subject instanceof IJsonObject subjectJson) {
                    error.put("subject", subjectJson.toJson());
                } else {
                    error.put("subject", subject.getPlayer());
                }

                if (Main.DEBUG) {
                    System.err.println(error.toString(2));
                    System.err.println(state.toString());
                }
                throw new Error(String.format("Failed to apply `%s` to `%s` given `%s`", name, subject, Arrays.toString(meta)));
            }
            subject.setLastUsage(name, timeOfAction);
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

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
            }
            throw new Error(String.format("Rule %s has cooldown of %d seconds but only waited %d seconds", name, cooldown, elapsed));
        }
    }

    @Override
    public boolean canApply(State state, T subject, Object... meta) {
        long cooldown = cooldownFunction.apply(state);
        long elapsed = (long) meta[0] - subject.getLastUsage(name);
        return elapsed >= cooldown && super.canApply(state, subject, Arrays.copyOfRange(meta, 1, meta.length));
    }

}
