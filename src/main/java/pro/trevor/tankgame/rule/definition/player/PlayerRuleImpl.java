package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;

import java.util.List;

public abstract class PlayerRuleImpl implements IPlayerRule {

    private final String name;
    private final String description;
    private final List<String> stages;

    public PlayerRuleImpl(String name, String description, String... stages) {
        this.name = name;
        this.description = description;
        this.stages = List.of(stages);
    }

    public final String name() {
        return name;
    }
    public final String getDescription() {
        return description;
    }
    public final List<String> getStages() {
        return stages;
    }

    private void canApplyOrThrow(PlayerRuleContext context) {
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

    // Subclasses must call super.apply(context)
    public void apply(PlayerRuleContext context) {
        canApplyOrThrow(context);
    }

    public abstract List<PlayerRuleError> canApply(PlayerRuleContext context);
    public abstract List<LogFieldSpec<?>> getFieldSpecs(PlayerRuleContext context);
}
