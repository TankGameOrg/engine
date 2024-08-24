package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriConsumer;

import java.util.Arrays;
import java.util.List;
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

        long cooldownEnd = player.getOrElse(Attribute.GLOBAL_COOLDOWN_END_TIME, 0L);

        if (cooldownEnd <= timeOfAction) {
            Object[] appliedMeta = Arrays.copyOfRange(meta, 1, meta.length);
            Result<List<String>> canApply = canApplyConditional(state, subject, appliedMeta);
            if (canApply.isOk()) {
                consumer.accept(state, subject, appliedMeta);
                player.put(Attribute.GLOBAL_COOLDOWN_END_TIME, timeOfAction + cooldown);
            } else {
                JSONObject error = new JSONObject();
                error.put("error", true);
                error.put("rule", name);
                error.put("subject", subject.toJson());
                if (Main.DEBUG) {
                    System.err.println(error.toString(2));
                    System.err.println(state);
                }
                StringBuilder sb = new StringBuilder(String.format("Cannot apply '%s' with subject '%s' and arguments %s:\n", name, subject.toString(), Arrays.toString(meta)));
                List<String> errors = canApply.getError();
                for (int i = 0; i < errors.size(); ++i) {
                    sb.append(errors.get(i));
                    if (i < errors.size() - 1) {
                        sb.append(",\n");
                    }
                }
                throw new Error(sb.toString());
            }
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);
            error.put("cooldown", cooldown);
            error.put("cooldown_end", cooldownEnd);
            error.put("subject", subject.toJson());

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
            }
            throw new Error(String.format("Rule %s has cooldown of %d seconds you have %d seconds remaining", name, cooldown, cooldownEnd - timeOfAction));
        }
    }

    @Override
    public boolean canApply(State state, PlayerRef subject, Object... meta) {
        Optional<Player> player = subject.toPlayer(state);
        if (player.isEmpty()) {
            throw new Error("No player found with name `" + subject.getName() + "`");
        }
        boolean cooldownEnded = ((long) meta[0]) >= player.get().getOrElse(Attribute.GLOBAL_COOLDOWN_END_TIME,0L);
        return cooldownEnded && super.canApply(state, subject, Arrays.copyOfRange(meta, 1, meta.length));
    }

}
