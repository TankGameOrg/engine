package pro.trevor.tankgame.rule.impl.shared.player;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.player.conditional.BasicRulePredicate;
import pro.trevor.tankgame.rule.definition.player.conditional.IRulePredicate;
import pro.trevor.tankgame.rule.definition.player.conditional.PredicateHelpers;
import pro.trevor.tankgame.rule.definition.player.conditional.RulePredicateStream;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.util.Result;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static pro.trevor.tankgame.util.Util.getSpacesInRange;

public class Helper {

    public static boolean playerHasLivingTank(PlayerRuleContext context) {
        return context.getState().getTankForPlayerRef(context.getPlayerRef()).map((tank) ->  !tank.getOrElse(Attribute.DEAD, false)).orElse(false);
    }

    public static <T extends Comparable<T>> boolean containerHasAttributeMinimum(AttributeContainer container, Attribute<T> attribute, T threshold) {
        return container.get(attribute).map((value) -> value.compareTo(threshold) >= 0).orElse(false);
    }

    public static <T extends Comparable<T>> boolean containerHasAttributeMaximum(AttributeContainer container, Attribute<T> attribute, T threshold) {
        return container.get(attribute).map((value) -> value.compareTo(threshold) < 0).orElse(false);
    }

    public static void assertFunctionReturnsTrue(PlayerRuleContext context, List<PlayerRuleError> errors, Predicate<PlayerRuleContext> predicate, PlayerRuleError.Category category, String format, Object... args) {
        if (!predicate.test(context)) {
            errors.add(new PlayerRuleError(category, format, args));
        }
    }

    public static void assertFunctionReturnsFalse(PlayerRuleContext context, List<PlayerRuleError> errors, Predicate<PlayerRuleContext> predicate, PlayerRuleError.Category category, String format, Object... args) {
        if (predicate.test(context)) {
            errors.add(new PlayerRuleError(category, format, args));
        }
    }

    public static class RulePredicate {

        public static IRulePredicate cofferCost(int cost) {
            return new RulePredicateStream<>(PredicateHelpers::getCouncil)
                    .map(PredicateHelpers.getAttribute(Attribute.COFFER))
                    .filter((context, coffer) -> coffer >= cost, new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Council does not have enough gold in the coffer"));
        }

        public static final IRulePredicate PLAYER_TANK_IS_ALIVE_PREDICATE = new RulePredicateStream<>(PredicateHelpers::getTank)
                .map(PredicateHelpers.getAttribute(Attribute.DEAD, false))
                .filter((context, dead) -> !dead, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Tank must be alive"));

        public static final IRulePredicate PLAYER_IS_COUNCIL_PREDICATE = new BasicRulePredicate((context) -> {
            return context.getState().getCouncil().allPlayersOnCouncil().contains(context.getPlayerRef());
        }, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Player must be a councilor"));

        // Check if TARGET_POSITION is within the subject's range
        public static final IRulePredicate TARGET_IS_IN_RANGE = new RulePredicateStream<>(PredicateHelpers::getTank)
                .filter(PredicateHelpers::hasLogEntry)
                .filter((context, tank) -> {
                    Position target = PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION);
                    return tank.getPosition().distanceFrom(target) <= tank.getOrElse(Attribute.RANGE, 0);
                }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target position is not in range"));

        public static final IRulePredicate TANK_HAS_ENOUGH_GOLD_TO_BUY_ACTION = new RulePredicateStream<>(PredicateHelpers::getTank)
                .filter(PredicateHelpers::hasLogEntry)
                .filter(PredicateHelpers.minimum(Attribute.GOLD, (context) -> PredicateHelpers.getLogField(context, Attribute.GOLD)));

        public static final IRulePredicate TARGET_TANK_IS_IN_RANGE = new RulePredicateStream<>(PredicateHelpers::getTank)
                .filter(PredicateHelpers::hasLogEntry)
                .filter((context, tank) -> {
                    Result<Tank, PlayerRuleError> result = PredicateHelpers.getTargetTank(context);
                    if (result.isError()) {
                        return Result.error(result.getError());
                    }

                    Tank targetTank = result.getValue();

                    Set<Position> positionsInRange = getSpacesInRange(context.getState().getBoard(), tank.getPosition(), tank.get(Attribute.RANGE).orElse(0));
                    if (!positionsInRange.contains(targetTank.getPosition())) {
                        return Result.error(new PlayerRuleError(PlayerRuleError.Category.INSUFFICENT_RESOURCES, "Tank has insufficient range"));
                    }

                    return Result.ok();
                });

        public static final IRulePredicate TARGET_POSITION_IS_EMPTY_SPACE = RulePredicateStream.empty()
                .filter(PredicateHelpers::hasLogEntry)
                .filter((context) -> context.getState().getBoard().isEmpty(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)), new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target space is not empty"));

        public static final IRulePredicate TARGET_TANK_IS_ALIVE = new RulePredicateStream<>(PredicateHelpers::getTargetTank)
                .map(PredicateHelpers.getAttribute(Attribute.DEAD, true))
                .filter((context, dead) -> !dead, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target must be a living tank"));

        public static final IRulePredicate TARGET_TANKS_SPEED_IS_NOT_MODIFIED = new RulePredicateStream<>(PredicateHelpers::getTargetTank)
                .filter((context, targetTank) -> {
                    return targetTank.getOrElse(Attribute.PREVIOUS_SPEED, targetTank.getUnsafe(Attribute.SPEED)).equals(targetTank.getUnsafe(Attribute.SPEED));
                }, new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Your target's speed has already been modified you can't modify it until the previous modification is removed"));

        public static final IRulePredicate TARGET_POSITION_IS_ON_BOARD = RulePredicateStream.empty()
                .filter(PredicateHelpers::hasLogEntry)
                .filter((context) -> context.getState().getBoard().isValidPosition(PredicateHelpers.getLogField(context, Attribute.TARGET_POSITION)),
                        new PlayerRuleError(PlayerRuleError.Category.GENERIC, "Target position is not within the game board"));

        public static final IRulePredicate PLAYER_TANK_IS_DEAD_PREDICATE = new BasicRulePredicate((context) -> {
            return context.getState().getTankForPlayerRef(context.getPlayerRef())
                    .map((tank) -> tank.getOrElse(Attribute.DEAD, false))
                    .orElse(true);
        }, new PlayerRuleError(PlayerRuleError.Category.NOT_APPLICABLE, "Player tank must be dead"));

    }

}
