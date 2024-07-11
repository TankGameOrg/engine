package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.IVarTriConsumer;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public class TimedPlayerConditionRule extends PlayerConditionRule {

    // Returns the cooldown for the function based on the state
    private final Function<State, Long> cooldownFunction;

    public TimedPlayerConditionRule(String name, Function<State, Long> cooldownFunction, RuleCondition condition, IVarTriConsumer<State, PlayerRef, Object> consumer, TypeRange<?>... parameters) {
        super(name, condition, consumer, parameters);
        this.cooldownFunction = cooldownFunction;
    }

    public TimedPlayerConditionRule(PlayerConditionRule rule, Function<State, Long> cooldownFunction) {
        super(rule.name, rule.condition, rule.consumer, rule.parameters);
        this.cooldownFunction = cooldownFunction;
    }

    @Override
    public void apply(State state, PlayerRef subject, Object... meta) {
        long timeOfAction = (long) meta[0];
        long cooldown = cooldownFunction.apply(state);
        Player player = subject.toPlayer(state).get();

        long elapsed = timeOfAction - Attribute.TIME_OF_LAST_ACTION.fromOrElse(player, 0L);
        if (elapsed >= cooldown) {
            Object[] appliedMeta = Arrays.copyOfRange(meta, 1, meta.length);
            if (super.canApply(state, subject, appliedMeta)) {
                consumer.accept(state, subject, appliedMeta);
            } else {
                JSONObject error = new JSONObject();
                error.put("error", true);
                error.put("rule", name);
                error.put("subject", subject.toJson());
                if (Main.DEBUG) {
                    System.err.println(error.toString(2));
                    System.err.println(state);
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
            error.put("subject", subject.toJson());

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
            }
            throw new Error(String.format("Rule %s has cooldown of %d seconds but only waited %d seconds", name, cooldown, elapsed));
        }
    }

    @Override
    public boolean canApply(State state, PlayerRef subject, Object... meta) {
        Optional<Player> player = subject.toPlayer(state);
        if (player.isEmpty()) {
            throw new Error("No player found with name `" + subject.getName() + "`");
        }
        long cooldown = cooldownFunction.apply(state);
        long elapsed = (long) meta[0] - Attribute.TIME_OF_LAST_ACTION.fromOrElse(player.get(),0L);
        return elapsed >= cooldown && super.canApply(state, subject, Arrays.copyOfRange(meta, 1, meta.length));
    }

}
