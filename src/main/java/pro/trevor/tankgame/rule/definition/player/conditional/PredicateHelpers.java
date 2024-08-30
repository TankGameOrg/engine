package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.rule.definition.player.conditional.RulePredicateStream;
import pro.trevor.tankgame.util.Result;

public abstract class PredicateHelpers {
    public static <T> BiFunction<PlayerRuleContext, T, Optional<PlayerRuleError>> hasLogEntry() {
        return (context, value) -> context.getLogEntry().isPresent() ? Optional.empty() : Optional.of(PlayerRuleError.insufficientData("A log entry is required"));
    }

    /**
     * Get an attribute from an attribute container or return an error if it isn't present
     */
    public static <T extends AttributeContainer, E> BiFunction<PlayerRuleContext, T, Result<E, PlayerRuleError>> attribute(Attribute<E> attribute) {
        return (context, container) -> {
            Optional<E> value = container.get(attribute);
            if(value.isEmpty()) {
                return Result.error(PlayerRuleError.generic("Expected %s to have the attribute %s", container, attribute));
            }

            return Result.ok(value.get());
        };
    }

    /**
     * Get an attribute from an attribute container or return a default value if it's not present
     */
    public static <T extends AttributeContainer, E> BiFunction<PlayerRuleContext, T, Result<E, PlayerRuleError>> attribute(Attribute<E> attribute, E defaultValue) {
        return (context, container) -> Result.ok(container.getOrElse(attribute, defaultValue));
    }

    /**
     * Ensure that the given attribute is greater to or equal to cost
     * @return
     */
    public static <T extends AttributeContainer> BiFunction<PlayerRuleContext, T, Optional<PlayerRuleError>> minimum(Attribute<Integer> attribute, int value) {
        return minimum(attribute, (context) -> value);
    }

    /**
     * Ensure that the given attribute is greater to or equal to cost
     * @return
     */
    public static <T extends AttributeContainer> BiFunction<PlayerRuleContext, T, Optional<PlayerRuleError>> minimum(Attribute<Integer> attribute, Function<PlayerRuleContext, Integer> valueFunction) {
        return (context, container) -> {
            Result<Integer, PlayerRuleError> result = PredicateHelpers.attribute(attribute).apply(context, container);
            if(result.isError()) {
                return Optional.of(result.getError());
            }

            Integer requiredCost = valueFunction.apply(context);
            Integer amount = result.getValue();
            if(amount.compareTo(requiredCost) < 0) {
                return Optional.of(
                    PlayerRuleError.insufficientResources("%s does not have enough %s needed %d but had %d",
                        context.getPlayerRef(), attribute.getName().toLowerCase(), requiredCost, amount));
            }

            return Optional.empty();
        };
    }

    /**
     * Create a stream from the tank assosiated with the player
     */
    public static RulePredicateStream<GenericTank> playerTankStream() {
        return new RulePredicateStream<>((context) -> {
            Optional<GenericTank> tank = context.getState().getTankForPlayerRef(context.getPlayerRef());
            if(tank.isEmpty()) {
                return Result.error(PlayerRuleError.notApplicable("Expected player %s to have a tank", context.getPlayerRef()));
            }

            return Result.ok(tank.get());
        });
    }

    private IRulePredicate PLAYER_TANK_IS_DEAD_PREDICATE = PredicateHelpers.playerTankStream()
        .map(PredicateHelpers.attribute(Attribute.DEAD, false))
        .filter((context, dead) -> !dead, PlayerRuleError.generic("Player tank must be dead"));
}
