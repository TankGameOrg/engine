package pro.trevor.tankgame.rule.impl.shared.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.definition.range.UnitRange;
import pro.trevor.tankgame.rule.definition.range.BooleanRange;
import pro.trevor.tankgame.rule.definition.range.DiscreteIntegerRange;
import pro.trevor.tankgame.rule.definition.range.DonateTankRange;
import pro.trevor.tankgame.rule.definition.range.FilteredTypeRange;
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
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.util.function.ITriPredicate;

import static pro.trevor.tankgame.util.Util.*;

public class PlayerRules {
    public static final PlayerActionRule<GenericTank> BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT = new PlayerActionRule<>(
            PlayerRules.ActionKeys.BUY_ACTION,
            (s, t, n) -> !Attribute.DEAD.fromOrElse(t, false) && Attribute.GOLD.fromOrElse(t, 0) >= 3,
            (s, t, n) -> {
                int gold = toType(n[0], Integer.class);
                int n5 = gold / 5;
                int rem = gold - n5 * 5;
                int n3 = rem / 3;
                assert rem == n3 * 3;

                Attribute.ACTION_POINTS.to(t, Attribute.ACTION_POINTS.fromOrElse(t, 0) + n5 * 2 + n3);
                Attribute.GOLD.to(t, Attribute.GOLD.unsafeFrom(t) - gold);
            },
            new FilteredTypeRange<GenericTank, Integer>(
                new DiscreteIntegerRange("gold", new HashSet<>(List.of(3, 5, 8, 10))),
                (state, tank, cost) -> Attribute.GOLD.from(tank).orElse(0) >= cost));

    public static <T extends GenericTank> PlayerActionRule<T> BuyActionWithGold(int actionCost, int maxBuys) {
        if (actionCost <= 0)
            throw new Error("Illegal Action Cost of " + actionCost + " gold. Must be positive and non-zero.");
        if (maxBuys <= 0)
            throw new Error("illegal max buys of " + maxBuys + ". Must be positive and non-zero.");

        DiscreteIntegerRange goldCostRange = new DiscreteIntegerRange("gold", IntStream.rangeClosed(1, maxBuys).map(n -> n * actionCost).boxed()
            .collect(Collectors.toSet()));

        return new PlayerActionRule<>(
                ActionKeys.BUY_ACTION,
                (s, tank, n) -> {
                    int attemptedGoldSpent = toType(n[0], Integer.class);
                    int attemptedBuys = attemptedGoldSpent / actionCost;

                    return !Attribute.DEAD.from(tank).orElse(false)
                            && (Attribute.GOLD.from(tank).orElse(0) >= attemptedGoldSpent)
                            && Attribute.ACTION_POINTS.in(tank) && (attemptedBuys <= maxBuys)
                            && (attemptedBuys * actionCost == attemptedGoldSpent);
                },
                (s, tank, n) -> {
                    int goldSpent = toType(n[0], Integer.class);
                    int boughtActions = goldSpent / actionCost;

                    Attribute.ACTION_POINTS.to(tank, Attribute.ACTION_POINTS.unsafeFrom(tank) + boughtActions);
                    Attribute.GOLD.to(tank, Attribute.GOLD.unsafeFrom(tank) - goldSpent);
                },
                new FilteredTypeRange<GenericTank, Integer>(goldCostRange, (state, tank, cost) -> Attribute.GOLD.from(tank).orElse(0) >= cost));
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetMoveRule(Attribute<Integer> attribute,
            Integer cost) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.MOVE,
                (s, t, n) -> !Attribute.DEAD.from(t).orElse(false) && attribute.from(t).orElse(0) >= cost
                        && canMoveTo(s, t.getPosition(), toType(n[0], Position.class)),
                (s, t, n) -> {
                    attribute.to(t, attribute.unsafeFrom(t) - cost);
                    s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                    t.setPosition(toType(n[0], Position.class));
                    s.getBoard().putUnit(t);
                },
                new MovePositionRange("target"));
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetUpgradeRangeRule(Attribute<Integer> attribute,
            Integer cost) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.UPGRADE_RANGE,
                (s, tank, n) -> !Attribute.DEAD.from(tank).orElse(false) &&
                        (attribute.from(tank).orElse(0) >= cost) && (Attribute.RANGE.in(tank)),
                (s, tank, n) -> {
                    Attribute.RANGE.to(tank, Attribute.RANGE.unsafeFrom(tank) + 1);
                    attribute.to(tank, attribute.unsafeFrom(tank) - cost);
                });
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetShareGoldWithTaxRule(int taxAmount) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.DONATE,
                (s, tank, n) -> {
                    GenericTank other = toType(n[0], GenericTank.class);
                    int donation = toType(n[1], Integer.class);

                    return donation > 0
                            && !Attribute.DEAD.from(tank).orElse(false)
                            && (Attribute.GOLD.from(tank).orElse(0) >= donation + taxAmount)
                            && Attribute.GOLD.in(other)
                            && getSpacesInRange(s.getBoard(), tank.getPosition(), Attribute.RANGE.from(tank).orElse(0))
                                    .contains(other.getPosition());
                },
                (s, tank, n) -> {
                    GenericTank other = toType(n[0], GenericTank.class);
                    int donation = toType(n[1], Integer.class);

                    Attribute.GOLD.to(tank, Attribute.GOLD.unsafeFrom(tank) - (donation + taxAmount));
                    Attribute.GOLD.to(other, Attribute.GOLD.unsafeFrom(other) + donation);
                    Attribute.COFFER.to(s.getCouncil(), Attribute.COFFER.unsafeFrom(s.getCouncil()) + taxAmount);
                },
                new DonateTankRange("target"),
                new IntegerRange("donation"));
    }

    public static PlayerActionRule<Council> GetCofferCostStimulusRule(int cost) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.STIMULUS,
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    return !Attribute.DEAD.from(t).orElse(false) && Attribute.ACTION_POINTS.in(t)
                            && Attribute.COFFER.unsafeFrom(s.getCouncil()) >= cost;
                },
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    Attribute.ACTION_POINTS.to(t, Attribute.ACTION_POINTS.unsafeFrom(t) + 1);
                    Attribute.COFFER.to(c, Attribute.COFFER.unsafeFrom(c) - cost);
                },
                UnitRange.ALL_LIVING_TANKS);
    }

    public static PlayerActionRule<Council> GetRuleCofferCostGrantLife(int cost, int minimumCouncillors) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.GRANT_LIFE,
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    return Attribute.DEAD.in(t) && Attribute.DURABILITY.in(t) && Attribute.COFFER.unsafeFrom(c) >= cost
                            && c.allPlayersOnCouncil().size() >= minimumCouncillors;
                },
                (s, c, n) -> {
                    Attribute.COFFER.to(c, Attribute.COFFER.unsafeFrom(c) - cost);
                    GenericTank t = toType(n[0], GenericTank.class);
                    if (Attribute.DEAD.unsafeFrom(t)) {
                        Attribute.DEAD.to(t, false);
                        Attribute.DURABILITY.to(t, 1);
                        s.getCouncil().getCouncillors().remove(t.getPlayerRef());
                    } else {
                        Attribute.DURABILITY.to(t, Attribute.DURABILITY.unsafeFrom(t) + 1);
                    }
                },
                UnitRange.ALL_TANKS);
    }

    public static PlayerActionRule<Council> GetRuleCofferCostBounty(int lowerBound, int upperBound) {
        assert lowerBound >= 0 && upperBound >= lowerBound;
        return new PlayerActionRule<>(PlayerRules.ActionKeys.BOUNTY,
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    int bounty = toType(n[1], Integer.class);
                    return !Attribute.DEAD.fromOrElse(t, false) && Attribute.CAN_BOUNTY.fromOrElse(c, true) &&
                            Attribute.COFFER.unsafeFrom(c) >= bounty;
                },
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    int bounty = toType(n[1], Integer.class);
                    assert Attribute.COFFER.unsafeFrom(c) >= bounty;
                    Attribute.BOUNTY.to(t, Attribute.BOUNTY.fromOrElse(t, 0) + bounty);
                    Attribute.COFFER.to(c, Attribute.COFFER.unsafeFrom(c) - bounty);
                    Attribute.CAN_BOUNTY.to(c, false);
                },
                UnitRange.ALL_LIVING_TANKS,
                new FilteredTypeRange<Council, Integer>(
                    new DiscreteIntegerRange("bounty", lowerBound, upperBound),
                    (state, council, bounty) -> Attribute.COFFER.fromOrElse(council, 0) >= bounty));
    }

    public static <T extends GenericTank> PlayerActionRule<T> SpendActionToShootGeneric(
            ITriPredicate<State, Position, Position> lineOfSight,
            ITriConsumer<State, T, IElement> handleHit) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.SHOOT,
                (s, t, n) -> s.getBoard().isValidPosition(toType(n[0], Position.class))
                            && !Attribute.DEAD.from(t).orElse(false)
                            && (Attribute.ACTION_POINTS.from(t).orElse(0) >= 1)
                            && (t.getPosition().distanceFrom(toType(n[0], Position.class)) <= Attribute.RANGE.from(t)
                                    .orElse(0))
                            && lineOfSight.test(s, t.getPosition(), toType(n[0], Position.class)),
                (s, t, n) -> {
                    Position target = toType(n[0], Position.class);
                    boolean hit = toType(n[1], Boolean.class);
                    Attribute.ACTION_POINTS.to(t, Attribute.ACTION_POINTS.unsafeFrom(t) - 1);

                    Optional<IElement> optionalElement = s.getBoard().getUnitOrFloor(target);
                    if (optionalElement.isEmpty()) {
                        throw new Error(
                                String.format("Target position %s is not on the game board", target.toString()));
                    }

                    if (hit) {
                        handleHit.accept(s, t, optionalElement.get());
                    }
                },
                new ShootPositionRange("target", lineOfSight),
                new BooleanRange("hit"));
    }

    public static <T extends GenericTank> PlayerActionRule<T> SpendActionToShootWithDeathHandle(
            ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<State, T, GenericTank> handleDeath) {
        return SpendActionToShootGeneric(lineOfSight, (s, t, element) -> {
            switch (element) {
                case GenericTank tank -> {
                    Attribute.DURABILITY.to(tank, Attribute.DURABILITY.unsafeFrom(tank) - 1);
                    if (!Attribute.DEAD.unsafeFrom(tank) && Attribute.DURABILITY.unsafeFrom(tank) == 0) {
                        handleDeath.accept(s, t, tank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - 1);
                case DestructibleFloor floor -> {
                    if (Attribute.DESTROYED.from(floor).orElse(false))
                        return;
                    Attribute.DURABILITY.to(floor, Attribute.DURABILITY.unsafeFrom(floor) - 1);
                    if (Attribute.DURABILITY.unsafeFrom(floor) == 0) {
                        Attribute.DESTROYED.to(floor, true);
                    }
                }
                default -> {

                }
            }
        });
    }

    public static final PlayerActionRule<GenericTank> SHOOT_V3 = SpendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV3,
            (s, t, d) -> Attribute.GOLD.to(t, Attribute.GOLD.fromOrElse(t, 0) + Attribute.GOLD.unsafeFrom(d) + Attribute.BOUNTY.unsafeFrom(d)));

    public static final PlayerActionRule<GenericTank> SHOOT_V4 = SpendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV4,
            (s, tank, dead) -> {
                Attribute.GOLD.to(tank, Attribute.GOLD.fromOrElse(tank, 0) + Attribute.BOUNTY.unsafeFrom(dead));
                switch (Attribute.GOLD.unsafeFrom(dead)) {
                    case 0 -> {
                    }
                    case 1 -> Attribute.GOLD.to(tank, Attribute.GOLD.fromOrElse(tank, 0) + 1);
                    default -> {
                        // Tax is target tank gold * 0.25 rounded up
                        int tax = (Attribute.GOLD.unsafeFrom(dead) + 2) / 4;
                        Attribute.GOLD.to(tank, Attribute.GOLD.fromOrElse(tank, 0) + Attribute.GOLD.unsafeFrom(dead) - tax);
                        Attribute.COFFER.to(s.getCouncil(), Attribute.COFFER.unsafeFrom(s.getCouncil()) + tax);
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