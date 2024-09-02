package pro.trevor.tankgame.rule.definition.actions;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

public interface ILogFieldSource<T> {
    LogFieldSpec<T> getLogFieldSpec(PlayerRuleContext context);
}
