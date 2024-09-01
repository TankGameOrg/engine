package pro.trevor.tankgame.rule.definition.player.conditional;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import pro.trevor.tankgame.log.LogEntry;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.util.Result;

public abstract class PredicateHelpers {
    /**
     * Get an attribute from an attribute container or return an error if it isn't present
     */
    public static <T extends AttributeContainer, E> BiFunction<PlayerRuleContext, T, Result<E, PlayerRuleError>> getAttribute(Attribute<E> attribute) {
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
    public static <T extends AttributeContainer, E> BiFunction<PlayerRuleContext, T, Result<E, PlayerRuleError>> getAttribute(Attribute<E> attribute, E defaultValue) {
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
            assert container != null;
            Result<Integer, PlayerRuleError> result = PredicateHelpers.getAttribute(attribute).apply(context, container);
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
     * Get the tank assosiated with the player who initiated the current context
     */
    public static Result<GenericTank, PlayerRuleError> getTank(PlayerRuleContext context) {
        Optional<GenericTank> tank = context.getState().getTankForPlayerRef(context.getPlayerRef());
        if(tank.isEmpty()) {
            return Result.error(PlayerRuleError.notApplicable("Expected player %s to have a tank", context.getPlayerRef()));
        }

        return Result.ok(tank.get());
    }

    /**
     * Get the the player who initiated the current context
     */
    public static Result<Player, PlayerRuleError> getPlayer(PlayerRuleContext context) {
        Optional<Player> player = context.getState().getPlayer(context.getPlayerRef());
        if(player.isEmpty()) {
            return Result.error(PlayerRuleError.notApplicable("Could not find player %s", context.getPlayerRef()));
        }

        return Result.ok(player.get());
    }

    /**
     * Get the requested field from the log entry associated with a context or *throw* an error if it's not present
     */
    public static <T> T getLogField(PlayerRuleContext context, Attribute<T> attribute) {
        Optional<LogEntry> entry = context.getLogEntry();
        if(entry.isEmpty()) {
            throw new Error("A log entry is required");
        }

        return entry.get().getUnsafe(attribute);
    }

    public static <T> Optional<PlayerRuleError> hasLogEntry(PlayerRuleContext context, Object unused) {
        return context.getLogEntry().isPresent() ?
            Optional.empty() :
            Optional.of(PlayerRuleError.insufficientData("A log entry is required"));
    }

    /**
     * Lookup the tank for the TARGET_PLAYER from the log entry
     */
    public static Result<GenericTank, PlayerRuleError> getTargetTank(PlayerRuleContext context) {
        Optional<LogEntry> optionalEntry = context.getLogEntry();
        if(optionalEntry.isEmpty()) {
            return Result.error(PlayerRuleError.insufficientData("A log entry is required"));
        }

        PlayerRef targetRef = optionalEntry.get().getUnsafe(Attribute.TARGET_PLAYER);
        Optional<GenericTank> optionalTank = context.getState().getTankForPlayerRef(targetRef);
        if(optionalTank.isEmpty()) {
            return Result.error(PlayerRuleError.generic("Could not find a tank for %s", targetRef));
        }

        return Result.ok(optionalTank.get());
    }

    public static Result<Council, PlayerRuleError> getCouncil(PlayerRuleContext context) {
        return Result.ok(context.getState().getCouncil());
    }
}
