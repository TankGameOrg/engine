package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;
import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerConditionRule implements IPlayerRule {

    private final String name;
    private final RuleCondition condition;
    private final Consumer<PlayerRuleContext> consumer;
    private final Function<PlayerRuleContext, List<LogFieldSpec<?>>> logFieldSource;

    public PlayerConditionRule(String name, RuleCondition condition, Consumer<PlayerRuleContext> consumer, Function<PlayerRuleContext, List<LogFieldSpec<?>>> logFieldSource) {
        this.name = name;
        this.condition = condition;
        this.consumer = consumer;
        this.logFieldSource = logFieldSource;
    }

    protected PlayerConditionRule(PlayerConditionRule rule) {
        this(rule.name, rule.condition, rule.consumer, rule.logFieldSource);
    }

    @Override
    public void apply(PlayerRuleContext context) {
        canApplyOrThrow(context);
        consumer.accept(context);
    }

    protected void canApplyOrThrow(PlayerRuleContext context) {
        List<PlayerRuleError> errors = canApply(context);
        if(!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder(String.format("Cannot apply '%s' with subject '%s' and arguments %s:\n", name, context.getPlayerRef(), context.getLogEntry()));
            for (int i = 0; i < errors.size(); ++i) {
                sb.append(errors.get(i));
                if (i < errors.size() - 1) {
                    sb.append(",\n");
                }
            }
            throw new Error(sb.toString());
        }
    }

    @Override
    public List<PlayerRuleError> canApply(PlayerRuleContext context) {
        return condition.test(context);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<LogFieldSpec<?>> getFieldSpecs(PlayerRuleContext context) {
        return logFieldSource.apply(context);
    }
}
