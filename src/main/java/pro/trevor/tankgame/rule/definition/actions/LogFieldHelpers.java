package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.conditional.PredicateHelpers;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Pair;
import pro.trevor.tankgame.util.Util;
import pro.trevor.tankgame.util.function.ITriPredicate;

public abstract class LogFieldHelpers {
    /**
     * Find all positions within RANGE of the current player's tank
     */
    public static Set<Position> getPositionsInRange(PlayerRuleContext context) {
        Board board = context.getState().getBoard();
        Tank tank = PredicateHelpers.getTank(context).getValue();

        return Util.getSpacesInRange(board, tank.getPosition(), tank.getOrElse(Attribute.RANGE, 0));
    }

    /**
     * Find all positions withing RANGE and line of sight of the current player's tank
     */
    public static Stream<Position> getTargetsInLineOfSight(PlayerRuleContext context, ITriPredicate<State, Position, Position> lineOfSight) {
        Tank tank = PredicateHelpers.getTank(context).getValue();

        return getPositionsInRange(context).stream()
            .filter((position) -> lineOfSight.test(context.getState(), tank.getPosition(), position));
    }

    /**
     * Make a basic EnumeratedLogFieldSpec from a stream
     */
    public static <T> EnumeratedLogFieldSpec<T> makeLogFieldSpec(Attribute<T> attribute, Stream<T> values) {
        List<LogFieldValueDescriptor<T>> options = values.map((value) -> new LogFieldValueDescriptor<>(value)).toList();
        return new EnumeratedLogFieldSpec<>(attribute, options);
    }

    /**
     * Get a list of players who are in range (but may not be in line of sight) of the current player
     */
    public static EnumeratedLogFieldSpec<PlayerRef> getPlayersInDonationRangeSpec(PlayerRuleContext context) {
        Board board = context.getState().getBoard();

        Stream<PlayerRef> players = getPositionsInRange(context).stream()
            .map((position) -> board.getUnit(position))
            .filter((optionalUnit) -> optionalUnit.isPresent() && optionalUnit.get() instanceof Tank)
            .map((optionalUnit) -> ((Tank) optionalUnit.get()).getPlayerRef());

        return makeLogFieldSpec(Attribute.TARGET_PLAYER, players);
    }

    /**
     * Get list of positions that the current player can move to
     */
    public static EnumeratedLogFieldSpec<Position> getMovablePositionsSpec(PlayerRuleContext context) {
        Tank tank = PredicateHelpers.getTank(context).getValue();
        Set<Position> movableSpaces = Util.allPossibleMoves(context.getState().getBoard(), tank.getPosition(), tank.getOrElse(Attribute.SPEED, 1));
        return makeLogFieldSpec(Attribute.TARGET_POSITION, movableSpaces.stream());
    }

    /**
     * Make a log field spec for a consecutive range of integers
     *
     * lower and upper are both inclusive; range : [lower, upper]
     */
    public static EnumeratedLogFieldSpec<Integer> getIntegerSpec(Attribute<Integer> attribute, int lower, int upper) {
        return makeLogFieldSpec(attribute, IntStream.rangeClosed(lower, upper).boxed());
    }

    /**
     * Make a spec for exchanging one resource for another
     * @param from The resource that is being converted from (this will go in the log entry)
     * @param to The resource we are converting to (not included in log entry)
     * @param options A stream of pairs of valid conversions in the form (from, to) i.e. trading 3 gold for 1 action would be (3, 1)
     */
    public static EnumeratedLogFieldSpec<Integer> getExchangeSpec(Attribute<Integer> from, Attribute<Integer> to, Stream<Pair<Integer, Integer>> fromOptions) {
        List<LogFieldValueDescriptor<Integer>> descriptors = fromOptions.map((option) -> {
            String message = String.format("%d %s -> %d %s", option.left(), from.getName().toLowerCase(), option.right(), to.getName().toLowerCase());
            return new LogFieldValueDescriptor<>(option.left(), message);
        }).toList();

        return new EnumeratedLogFieldSpec<>(from, descriptors);
    }

    /**
     * Get the players assosiated with all of the tanks that match the filter
     */
    public static EnumeratedLogFieldSpec<PlayerRef> getAllTanksWithFilter(PlayerRuleContext context, Predicate<Tank> filter) {
        Stream<PlayerRef> players = context.getState().getBoard()
            .gatherUnits(Tank.class).stream()
            .filter(filter)
            .map((tank) -> tank.getPlayerRef());

        return makeLogFieldSpec(Attribute.TARGET_PLAYER, players);
    }

    /**
     * Get the players assosiated with all of the living tanks
     */
    public static EnumeratedLogFieldSpec<PlayerRef> getAllPlayersWithLivingTanksSpec(PlayerRuleContext context) {
        return getAllTanksWithFilter(context, (tank) -> !tank.getOrElse(Attribute.DEAD, false));
    }

    /**
     * Get the players assosiated with all of the tanks
     */
    public static EnumeratedLogFieldSpec<PlayerRef> getAllPlayersWithTanksSpec(PlayerRuleContext context) {
        return getAllTanksWithFilter(context, (tank) -> true);
    }

    /**
     * Get a list of positions that the player can shoot plus a list of additional fields that the client must provide if that position is selected
     * @param context The context to generate positions for
     * @param lineOfSight A function that determines if a player has line of sight to a position
     * @param additionalFieldsFactory A factory that returns a list of addtional fields that should be supplied for a given position
     */
    public static EnumeratedLogFieldSpec<Position> getShootablePositionsSpec(PlayerRuleContext context, ITriPredicate<State, Position, Position> lineOfSight, Function<Position, List<LogFieldSpec<?>>> additionalFieldsFactory) {
        List<LogFieldValueDescriptor<Position>> descriptors = getTargetsInLineOfSight(context, lineOfSight)
            .map((position) -> new LogFieldValueDescriptor<>(position, additionalFieldsFactory.apply(position)))
            .toList();

        return new EnumeratedLogFieldSpec<>(Attribute.TARGET_POSITION, descriptors);
    }

    /**
     * Get a list of positions that are currently empty
     */
    public static EnumeratedLogFieldSpec<Position> getEmptyPositionsSpec(PlayerRuleContext context) {
        Board board = context.getState().getBoard();
        Stream<Position> positions = board.getAllPositions().stream()
            .filter((position) -> board.isEmpty(position));

        return makeLogFieldSpec(Attribute.TARGET_POSITION, positions);
    }
}
