package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.IVarTriConsumer;

import java.util.Arrays;
import java.util.List;

public class PlayerConditionRule implements IPlayerRule {

    protected final String name;
    protected final RuleCondition condition;
    protected final IVarTriConsumer<State, PlayerRef, Object> consumer;
    protected final TypeRange<?>[] parameters;

    public PlayerConditionRule(String name, RuleCondition condition, IVarTriConsumer<State, PlayerRef, Object> consumer, TypeRange<?>... parameters) {
        this.name = name;
        this.condition = condition;
        this.consumer = consumer;
        this.parameters = parameters;
    }

    @Override
    public void apply(State state, PlayerRef subject, Object... meta) {
        Result<List<String>> canApply = canApplyConditional(state, subject, meta);
        if (canApply.isOk()) {
            consumer.accept(state, subject, meta);
        } else {
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
    }

    @Override
    public boolean canApply(State state, PlayerRef subject, Object... meta) {
        return validateOptionalTypes(meta) && condition.test(state, subject, meta).isOk();
    }

    /**
     * Get a result indicating whether this action can be applied before a log entry has been created.
     * The result may change depending on the contents of the actual log entry so the overloaded version
     * should always be called after a log entry is created.
     *
     * @param state The state to test
     * @param subject The player who will be applying the rule
     * @return A result indicating if the rule can be applied or the reason(s) why it can't be
     */
    public Result<List<String>> canApplyConditionalWithoutMeta(State state, PlayerRef subject) {
        return condition.testForPossibleActions(state, subject);
    }

    /**
     * Get a result indicating whether this action can be applied before a log entry has been created.
     * 
     * @param state The state to test
     * @param subject The player who will be appling the rule
     * @param meta The metadata from the log entry
     * @return A result indicating if the rule can be applied or the reason(s) why it can't be
     */
    public Result<List<String>> canApplyConditional(State state, PlayerRef subject, Object... meta) {
        if (validateOptionalTypes(meta)) {
            return condition.test(state, subject, meta);
        } else {
            return Result.error(List.of("Arguments given to conditional do not match expected argument types"));
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TypeRange<?>[] parameters() {
        return parameters;
    }

    protected boolean validateOptionalTypes(Object[] meta) {
        if (meta.length != parameters.length) {
            return false;
        }
        for (int i = 0; i < parameters.length; ++i) {
            if (!parameters[i].getBoundClass().isAssignableFrom(meta[i].getClass())) {
                return false;
            }
        }
        return true;
    }
}
