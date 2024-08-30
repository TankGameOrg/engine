package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.Optional;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;

public interface IRulePredicate {
    Optional<PlayerRuleError> test(PlayerRuleContext context);
}
