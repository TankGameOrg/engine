package pro.trevor.tankgame.rule.impl.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.player.conditional.*;
import pro.trevor.tankgame.rule.definition.range.UnitRange;
import pro.trevor.tankgame.rule.definition.range.BooleanRange;
import pro.trevor.tankgame.rule.definition.range.DiscreteIntegerRange;
import pro.trevor.tankgame.rule.definition.range.DonateTankRange;
import pro.trevor.tankgame.rule.definition.range.IntegerRange;
import pro.trevor.tankgame.rule.definition.range.MovePositionRange;
import pro.trevor.tankgame.rule.definition.range.ShootPositionRange;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.IElement;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.DestructibleFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.util.function.ITriPredicate;

import static pro.trevor.tankgame.util.Util.*;

public class PlayerRules {

    private static boolean hasTank(State state, PlayerRef playerRef) {
        return state.getBoard().gatherUnits(GenericTank.class).stream().map(GenericTank::getPlayerRef).anyMatch(playerRef::equals);
    }

    private static GenericTank getTank(State state, PlayerRef playerRef) {
        return state.getBoard().gatherUnits(GenericTank.class).stream().filter((t) -> t.getPlayerRef().equals(playerRef)).findAny().get();
    }

    private static boolean isCouncil(State state, PlayerRef playerRef) {
        return playerRef.getName().equals("Council") || state.getCouncil().allPlayersOnCouncil().contains(playerRef);
    }

    private static Council getCouncil(State state, PlayerRef playerRef) {
        return state.getCouncil();
    }

    private static final RulePredicate PLAYER_HAS_TANK_PREDICATE = new RulePredicate((state, player, n) ->
            hasTank(state, player), "Player has no corresponding tank");

    private static final RulePredicate TANK_IS_ALIVE_PREDICATE = new BooleanPredicate<>(PlayerRules::getTank, Attribute.DEAD, false, "Tank must not be dead");

    private static final RulePredicate PLAYER_IS_COUNCIL_PREDICATE = new RulePredicate((state, player, n) -> isCouncil(state, player), "Player is not council");

    public static final PlayerConditionRule BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT = new PlayerConditionRule(
            PlayerRules.ActionKeys.BUY_ACTION,
            new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                    new GetterPredicate<>(PlayerRules::getTank,
                            (state, tank, n) -> toType(n[0], Integer.class) <= tank.getOrElse(Attribute.GOLD, 0),
                            "Tank has insufficient gold")
            ),
            (state, player, n) -> {
                GenericTank tank = getTank(state, player);
                int gold = toType(n[0], Integer.class);
                int n5 = gold / 5;
                int rem = gold - n5 * 5;
                int n3 = rem / 3;
                assert rem == n3 * 3;

                Attribute.ACTION_POINTS.to(tank, tank.getOrElse(Attribute.ACTION_POINTS, 0) + n5 * 2 + n3);
                Attribute.GOLD.to(tank, tank.getUnsafe(Attribute.GOLD) - gold);
            },
            new DiscreteIntegerRange("gold", new HashSet<>(List.of(3, 5, 8, 10))));

    public static PlayerConditionRule buyActionWithGold(int actionCost, int maxBuys) {
        if (actionCost <= 0)
            throw new Error("Illegal Action Cost of " + actionCost + " gold. Must be positive and non-zero.");
        if (maxBuys <= 0)
            throw new Error("Illegal max buys of " + maxBuys + ". Must be positive and non-zero.");

        return new PlayerConditionRule(ActionKeys.BUY_ACTION,
                new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                        new GetterPredicate<>(PlayerRules::getTank,
                                (state, tank, n) -> toType(n[0], Integer.class) <= tank.getOrElse(Attribute.GOLD, 0), "Tank has insufficient gold"),
                        new RulePredicate((state, player, n) -> toType(n[0], Integer.class) / actionCost <= maxBuys, "Actions bought must be fewer than or equal to " + maxBuys),
                        new RulePredicate((state, player, n) -> toType(n[0], Integer.class) % actionCost == 0, "Gold spent must be a multiple of the action cost: " + actionCost)
                ),
                (state, player, n) -> {
                    GenericTank tank = getTank(state, player);
                    int goldSpent = toType(n[0], Integer.class);
                    int boughtActions = goldSpent / actionCost;

                    Attribute.ACTION_POINTS.to(tank, tank.getUnsafe(Attribute.ACTION_POINTS) + boughtActions);
                    Attribute.GOLD.to(tank, tank.getUnsafe(Attribute.GOLD) - goldSpent);
                },
                new DiscreteIntegerRange("gold", IntStream.rangeClosed(1, maxBuys).map(n -> n * actionCost).boxed()
                        .collect(Collectors.toSet())));
    }

    public static PlayerConditionRule getMoveRule(Attribute<Integer> attribute, int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.MOVE,
                new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                        new MinimumPredicate<>(PlayerRules::getTank, attribute, cost, "Tank has insufficient " + attribute.getName()),
                        new GetterPredicate<>(PlayerRules::getTank,
                                (state, tank, n)-> canMoveTo(state, tank.getPosition(), toType(n[0], Position.class)),
                                "Tank cannot move to target position"))
                ,
                (state, player, n) -> {
                    GenericTank tank = getTank(state, player);
                    attribute.to(tank, tank.getUnsafe(attribute) - cost);
                    state.getBoard().putUnit(new EmptyUnit(tank.getPosition()));
                    tank.setPosition(toType(n[0], Position.class));
                    state.getBoard().putUnit(tank);
                },
                new MovePositionRange("target"));
    }

    public static PlayerConditionRule getUpgradeRangeRule(Attribute<Integer> attribute, int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.UPGRADE_RANGE,
                new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                        new MinimumPredicate<>(PlayerRules::getTank, attribute, cost, "Tank has insufficient " + attribute.getName())),
                (state, player, n) -> {
                    GenericTank tank = getTank(state, player);
                    Attribute.RANGE.to(tank, tank.getOrElse(Attribute.RANGE, 0) + 1);
                    attribute.to(tank, tank.getUnsafe(attribute) - cost);
                });
    }

    public static PlayerConditionRule getShareGoldWithTaxRule(int taxAmount) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.DONATE,
                new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                        new GetterPredicate<>(PlayerRules::getTank, (state, tank, n) ->
                            tank.getOrElse(Attribute.GOLD,0) >= toType(n[1], Integer.class) + taxAmount,
                                "Tank has insufficient gold"),
                        new GetterPredicate<>(PlayerRules::getTank, (state, tank, n) ->
                                getSpacesInRange(state.getBoard(), tank.getPosition(),
                                        tank.get(Attribute.RANGE).orElse(0)).contains(toType(n[0], GenericTank.class).getPosition()),
                                "Tank has insufficient gold"),
                        new RulePredicate((state, player, n) -> toType(n[1], Integer.class) >= 0,  "Donation must be positive"),
                        new RulePredicate((state, player, n) -> toType(n[0], GenericTank.class).has(Attribute.GOLD),  "Target must have gold attribute")
                ),
                (state, player, n) -> {
                    GenericTank tank = getTank(state, player);
                    GenericTank other = toType(n[0], GenericTank.class);
                    int donation = toType(n[1], Integer.class);

                    Attribute.GOLD.to(tank, tank.getUnsafe(Attribute.GOLD) - (donation + taxAmount));
                    Attribute.GOLD.to(other, other.getUnsafe(Attribute.GOLD) + donation);
                    Attribute.COFFER.to(state.getCouncil(), state.getCouncil().getUnsafe(Attribute.COFFER) + taxAmount);
                },
                new DonateTankRange("target"),
                new IntegerRange("donation"));
    }

    public static PlayerConditionRule getCofferCostStimulusRule(int cost) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.STIMULUS,
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                        new MinimumPredicate<>(PlayerRules::getCouncil, Attribute.COFFER, cost, "Council has insufficient coffer"),
                        new RulePredicate((state, player, n) -> !toType(n[0], GenericTank.class).getOrElse(Attribute.DEAD, false), "Target tank must not be dead")
                ),
                (state, player, n) -> {
                    Council council = state.getCouncil();
                    GenericTank t = toType(n[0], GenericTank.class);
                    Attribute.ACTION_POINTS.to(t, t.getOrElse(Attribute.ACTION_POINTS, 0) + 1);
                    Attribute.COFFER.to(council, council.getUnsafe(Attribute.COFFER) - cost);
                },
                UnitRange.ALL_LIVING_TANKS);
    }

    public static PlayerConditionRule getRuleCofferCostGrantLife(int cost, int minimumCouncillors) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.GRANT_LIFE,
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                        new MinimumPredicate<>(PlayerRules::getCouncil, Attribute.COFFER, cost, "Council has insufficient coffer"),
                        new RulePredicate((state, player, n) -> state.getCouncil().allPlayersOnCouncil().size() >= minimumCouncillors, "Council has insufficient members"),
                        new RulePredicate((state, player, n) -> toType(n[0], GenericTank.class).has(Attribute.DURABILITY), "Target tank must have durability")
                ),
                (state, player, n) -> {
                    Council council = state.getCouncil();
                    Attribute.COFFER.to(council, council.getUnsafe(Attribute.COFFER) - cost);
                    GenericTank t = toType(n[0], GenericTank.class);
                    if (t.getOrElse(Attribute.DEAD, false)) {
                        Attribute.DEAD.to(t, false);
                        Attribute.DURABILITY.to(t, 1);
                        council.getCouncillors().remove(t.getPlayerRef());
                    } else {
                        Attribute.DURABILITY.to(t, t.getUnsafe(Attribute.DURABILITY) + 1);
                    }
                },
                UnitRange.ALL_TANKS);
    }

    public static PlayerConditionRule getRuleCofferCostBounty(int lowerBound, int upperBound) {
        assert lowerBound >= 0 && upperBound >= lowerBound;
        return new PlayerConditionRule(PlayerRules.ActionKeys.BOUNTY,
                new RuleCondition(PLAYER_IS_COUNCIL_PREDICATE,
                        new BooleanPredicate<>(PlayerRules::getCouncil, Attribute.CAN_BOUNTY, true, "Council cannot bounty"),
                        new RulePredicate((state, player, n) -> !toType(n[0], GenericTank.class).getOrElse(Attribute.DEAD, false), "Target tank must not be dead"),
                        new RulePredicate((state, player, n) -> state.getCouncil().getOrElse(Attribute.COFFER, 0) >= toType(n[1], Integer.class), "Council has insufficient coffer")
                ),
                (state, player, n) -> {
                    Council council = state.getCouncil();
                    GenericTank t = toType(n[0], GenericTank.class);
                    int bounty = toType(n[1], Integer.class);
                    assert council.getUnsafe(Attribute.COFFER) >= bounty;
                    Attribute.BOUNTY.to(t, t.getOrElse(Attribute.BOUNTY, 0) + bounty);
                    Attribute.COFFER.to(council, council.getUnsafe(Attribute.COFFER) - bounty);
                    Attribute.CAN_BOUNTY.to(council, false);
                },
                UnitRange.ALL_LIVING_TANKS,
                new DiscreteIntegerRange("bounty", lowerBound, upperBound));
    }

    public static PlayerConditionRule spendActionToShootGeneric(
            ITriPredicate<State, Position, Position> lineOfSight,
            ITriConsumer<State, GenericTank, IElement> handleHit) {
        return new PlayerConditionRule(PlayerRules.ActionKeys.SHOOT,
                new RuleCondition(PLAYER_HAS_TANK_PREDICATE, TANK_IS_ALIVE_PREDICATE,
                        new MinimumPredicate<>(PlayerRules::getTank, Attribute.ACTION_POINTS, 1, "Tank has insufficient action points"),
                        new GetterPredicate<>(PlayerRules::getTank, (state, tank, n) ->
                                tank.getPosition().distanceFrom(toType(n[0], Position.class)) <= tank.getOrElse(Attribute.RANGE, 0), "Target position is not in range"),
                        new RulePredicate((state, player, n) -> state.getBoard().isValidPosition(toType(n[0], Position.class)), "Target position is not within the game board"),
                        new GetterPredicate<>(PlayerRules::getTank, (state, tank, n) -> lineOfSight.test(state, tank.getPosition(), toType(n[0], Position.class)), "Target position is not in line-of-sight")),
                (state, player, n) -> {
                    GenericTank tank = getTank(state, player);
                    Position target = toType(n[0], Position.class);
                    boolean hit = toType(n[1], Boolean.class);
                    Attribute.ACTION_POINTS.to(tank, tank.getUnsafe(Attribute.ACTION_POINTS) - 1);

                    Optional<IElement> optionalElement = state.getBoard().getUnitOrFloor(target);
                    if (optionalElement.isEmpty()) {
                        throw new Error(String.format("Target position %s is not on the game board", target.toString()));
                    }

                    if (hit) {
                        handleHit.accept(state, tank, optionalElement.get());
                    }
                },
                new ShootPositionRange("target", lineOfSight),
                new BooleanRange("hit"));
    }

    public static PlayerConditionRule spendActionToShootWithDeathHandle(
            ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<State, GenericTank, GenericTank> handleDeath) {
        return spendActionToShootGeneric(lineOfSight, (state, tank, element) -> {
            switch (element) {
                case GenericTank otherTank -> {
                    Attribute.DURABILITY.to(otherTank, otherTank.getUnsafe(Attribute.DURABILITY) - 1);
                    if (!otherTank.getUnsafe(Attribute.DEAD) && otherTank.getUnsafe(Attribute.DURABILITY) == 0) {
                        handleDeath.accept(state, tank, otherTank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - 1);
                case DestructibleFloor floor -> {
                    if (floor.get(Attribute.DESTROYED).orElse(false))
                        return;
                    Attribute.DURABILITY.to(floor, floor.getUnsafe(Attribute.DURABILITY) - 1);
                    if (floor.getUnsafe(Attribute.DURABILITY) == 0) {
                        Attribute.DESTROYED.to(floor, true);
                    }
                }
                default -> {
                }
            }
        });
    }

    public static final PlayerConditionRule SHOOT_V3 = spendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV3,
            (s, t, d) -> Attribute.GOLD.to(t, t.getOrElse(Attribute.GOLD, 0) + d.getUnsafe(Attribute.GOLD) + d.getUnsafe(Attribute.BOUNTY)));

    public static final PlayerConditionRule SHOOT_V4 = spendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV4,
            (s, tank, dead) -> {
                Attribute.GOLD.to(tank, tank.getOrElse(Attribute.GOLD, 0) + dead.getUnsafe(Attribute.BOUNTY));
                switch (dead.getUnsafe(Attribute.GOLD)) {
                    case 0 -> {
                    }
                    case 1 -> Attribute.GOLD.to(tank, tank.getOrElse(Attribute.GOLD, 0) + 1);
                    default -> {
                        // Tax is target tank gold / 4, rounded
                        int tax = (dead.getUnsafe(Attribute.GOLD) + 2) / 4;
                        Attribute.GOLD.to(tank, tank.getOrElse(Attribute.GOLD, 0) + dead.getUnsafe(Attribute.GOLD) - tax);
                        Attribute.COFFER.to(s.getCouncil(), s.getCouncil().getUnsafe(Attribute.COFFER) + tax);
                    }
                }
            });

    public static class ActionKeys {

        public static final String SHOOT = "shoot";
        public static final String MOVE = "move";
        public static final String DONATE = "donate";
        public static final String BUY_ACTION = "buy_action";
        public static final String UPGRADE_RANGE = "upgrade_range";

        public static final String STIMULUS = "stimulus";
        public static final String GRANT_LIFE = "grant_life";
        public static final String BOUNTY = "bounty";
    }
}