package pro.trevor.tankgame.rule.definition.player.stage;

import pro.trevor.tankgame.rule.definition.player.PlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;

public interface IPlayerRuleStage {
    Object apply(PlayerRuleContext context, PlayerRule rule);
}
