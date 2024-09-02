package pro.trevor.tankgame.rule.definition.player.conditional;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.util.Result;

public interface IRulePredicate {
    Result<Void, PlayerRuleError> test(PlayerRuleContext context);
}
