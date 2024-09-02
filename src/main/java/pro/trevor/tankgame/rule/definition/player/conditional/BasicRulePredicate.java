package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.function.Predicate;
import java.util.Optional;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;

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

    public Optional<PlayerRuleError> test(PlayerRuleContext context) {
        return predicate.test(context) ? Optional.empty() : Optional.of(error);
    }
}
