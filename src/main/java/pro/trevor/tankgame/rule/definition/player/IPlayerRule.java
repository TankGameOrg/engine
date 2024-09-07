package pro.trevor.tankgame.rule.definition.player;

import java.util.List;

import pro.trevor.tankgame.rule.definition.actions.LogFieldSpec;

public interface IPlayerRule {
    void apply(PlayerRuleContext context);
    List<PlayerRuleError> canApply(PlayerRuleContext context);

    String name();
    String getDescription();
    List<LogFieldSpec<?>> getFieldSpecs(PlayerRuleContext context);
}
