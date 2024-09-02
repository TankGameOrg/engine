package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.function.Predicate;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.util.Result;

public class BasicRulePredicate implements IRulePredicate {
    private Predicate<PlayerRuleContext> predicate;
    private PlayerRuleError error;

    public BasicRulePredicate(Predicate<PlayerRuleContext> predicate, PlayerRuleError error) {
        this.predicate = predicate;
        this.error = error;
    }

    public BasicRulePredicate(Predicate<PlayerRuleContext> predicate, String message) {
        this(predicate, new PlayerRuleError(PlayerRuleError.Category.GENERIC, message));
    }

    public Result<Void, PlayerRuleError> test(PlayerRuleContext context) {
        return predicate.test(context) ? Result.ok() : Result.error(error);
    }
}
