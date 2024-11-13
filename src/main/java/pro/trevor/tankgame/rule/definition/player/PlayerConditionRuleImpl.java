package pro.trevor.tankgame.rule.definition.player;

import pro.trevor.tankgame.rule.definition.player.conditional.RuleCondition;

import java.util.List;

public abstract class PlayerConditionRuleImpl extends PlayerRuleImpl {

    private final RuleCondition precondition;
    private final RuleCondition condition;

    public PlayerConditionRuleImpl(String name, String description, RuleCondition precondition, RuleCondition condition, String... stages) {
        super(name, description, stages);
        this.precondition = precondition;
        this.condition = condition;
    }

    public List<PlayerRuleError> canApply(PlayerRuleContext context) {
        List<PlayerRuleError> errors = precondition.test(context);
        return errors.isEmpty() ? condition.test(context) : errors;
    }
}
