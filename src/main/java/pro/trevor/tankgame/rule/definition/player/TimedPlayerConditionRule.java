package pro.trevor.tankgame.rule.definition.player;

import java.time.Instant;
import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TimedPlayerConditionRule extends PlayerConditionRule {

    // Returns the cooldown for the function based on the state
    private final Function<PlayerRuleContext, Long> cooldownFunction;

    public TimedPlayerConditionRule(PlayerConditionRule rule, Function<PlayerRuleContext, Long> cooldownFunction) {
        super(rule);
        this.cooldownFunction = cooldownFunction;
    }

    @Override
    public void apply(PlayerRuleContext context) {
        canApplyOrThrow(context);

        State state = context.getState();
        PlayerRef subject = context.getPlayerRef();
        long timeOfAction = context.getLogEntry().get().getOrElse(Attribute.TIMESTAMP, 0L);
        long cooldown = cooldownFunction.apply(context);
        Player player = subject.toPlayer(state).get();

        long cooldownEnd = player.getOrElse(Attribute.GLOBAL_COOLDOWN_END_TIME, 0L);

        if (cooldownEnd <= timeOfAction) {
            super.apply(context);
            player.put(Attribute.GLOBAL_COOLDOWN_END_TIME, timeOfAction + cooldown);
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name());
            error.put("cooldown", cooldown);
            error.put("cooldown_end", cooldownEnd);
            error.put("subject", subject.toJson());

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
            }
            throw new Error(String.format("Rule %s has cooldown of %d seconds you have %d seconds remaining", name(), cooldown, cooldownEnd - timeOfAction));
        }
    }

    @Override
    public List<PlayerRuleError> canApply(PlayerRuleContext context) {
        PlayerRef subject = context.getPlayerRef();
        Optional<Player> player = subject.toPlayer(context.getState());
        if (player.isEmpty()) {
            throw new Error("No player found with name `" + subject.getName() + "`");
        }

        List<PlayerRuleError> ruleErrors = new ArrayList<>(super.canApply(context));

        Optional<LogEntry> logEntry = context.getLogEntry();
        long timestamp = System.currentTimeMillis() / 1000L;
        if(logEntry.isPresent()) {
            timestamp = logEntry.get().getOrElse(Attribute.TIMESTAMP, 0L);
        }

        long cooldownEnd = player.get().getOrElse(Attribute.GLOBAL_COOLDOWN_END_TIME, 0L);
        if(timestamp < player.get().getOrElse(Attribute.GLOBAL_COOLDOWN_END_TIME, 0L)) {
            String cooldownEndTime = Instant.ofEpochSecond(cooldownEnd).toString();
            ruleErrors.add(
                new TimedPlayerRuleError(
                    PlayerRuleError.Category.COOLDOWN,
                    cooldownEnd,
                    "Action '%s' is on cooldown can cannot be taken until %s", name(), cooldownEndTime));
        }

        return ruleErrors;
    }

}
