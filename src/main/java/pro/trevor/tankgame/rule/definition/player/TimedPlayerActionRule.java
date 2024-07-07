package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.function.IVarTriConsumer;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class TimedPlayerActionRule<T extends IPlayerElement> extends PlayerActionRule<T> {

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
        Player player = subject.getPlayerRef().toPlayer(state).get();

        long elapsed = timeOfAction - Attribute.TIME_OF_LAST_ACTION.fromOrElse(player, 0L);
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
                    error.put("subject", subject.getPlayerRef());
                }

                if (Main.DEBUG) {
                    System.err.println(error.toString(2));
                    System.err.println(state.toString());
                }
                throw new Error(String.format("Failed to apply `%s` to `%s` given `%s`", name, subject, Arrays.toString(meta)));
            }
            Attribute.TIME_OF_LAST_ACTION.to(player, timeOfAction);
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);
            error.put("cooldown", cooldown);
            error.put("elapsed", elapsed);

            if (subject instanceof IJsonObject subjectJson) {
                error.put("subject", subjectJson.toJson());
            } else {
                error.put("subject", subject.getPlayerRef());
            }

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
            }
            throw new Error(String.format("Rule %s has cooldown of %d seconds but only waited %d seconds", name, cooldown, elapsed));
        }
    }

    @Override
    public boolean canApply(State state, T subject, Object... meta) {
        Optional<Player> player = subject.getPlayerRef().toPlayer(state);
        if (player.isEmpty()) {
            throw new Error("No player found with name `" + subject.getPlayerRef().getName() + "`");
        }
        long cooldown = cooldownFunction.apply(state);
        long elapsed = (long) meta[0] - Attribute.TIME_OF_LAST_ACTION.fromOrElse(player.get(),0L);
        return elapsed >= cooldown && super.canApply(state, subject, Arrays.copyOfRange(meta, 1, meta.length));
    }

    @Override
    public boolean canApply(State state, T subject) {
        // We don't know when this action is being applied so we can't check the cooldown
        return super.canApply(state, subject);
    }

}
