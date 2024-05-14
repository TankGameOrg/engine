package pro.trevor.tankgame.rule.impl.shared.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.range.DonateTankRange;
import pro.trevor.tankgame.rule.impl.shared.range.MovePositionRange;
import pro.trevor.tankgame.rule.impl.shared.range.ShootPositionRange;
import pro.trevor.tankgame.rule.impl.shared.range.TankRange;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.attribute.BaseAttribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.util.function.ITriPredicate;
import pro.trevor.tankgame.util.range.BooleanRange;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;
import pro.trevor.tankgame.util.range.IntegerRange;

import static pro.trevor.tankgame.util.Util.*;

public class PlayerRules {
    public static final PlayerActionRule<Tank> BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT = new PlayerActionRule<>(
            PlayerRules.ActionKeys.BUY_ACTION,
            (s, t, n) -> !t.isDead() && t.getGold() >= 3,
            (s, t, n) -> {
                int gold = toType(n[0], Integer.class);
                int n5 = gold / 5;
                int rem = gold - n5 * 5;
                int n3 = rem / 3;
                assert rem == n3 * 3;

                t.setActions(t.getActions() + n5 * 2 + n3);
                t.setGold(t.getGold() - gold);
            },
            new DiscreteIntegerRange("gold", new HashSet<>(List.of(3, 5, 8, 10))));

    public static <T extends GenericTank> PlayerActionRule<T> BuyActionWithGold(int actionCost, int maxBuys) {
        if (actionCost <= 0)
            throw new Error("Illegal Action Cost of " + actionCost + " gold. Must be positive and non-zero.");
        if (maxBuys <= 0)
            throw new Error("illegal max buys of " + maxBuys + ". Must be positive and non-zero.");

        return new PlayerActionRule<T>(
                ActionKeys.BUY_ACTION,
                (s, tank, n) -> {
                    int attemptedGoldSpent = toType(n[0], Integer.class);
                    int attemptedBuys = attemptedGoldSpent / actionCost;

                    return !Attributes.DEAD.from(tank).orElse(false)
                            && (Attributes.GOLD.from(tank).orElse(0) >= attemptedGoldSpent)
                            && Attributes.ACTION_POINTS.in(tank) && (attemptedBuys <= maxBuys)
                            && (attemptedBuys * actionCost == attemptedGoldSpent);
                },
                (s, tank, n) -> {
                    int goldSpent = toType(n[0], Integer.class);
                    int boughtActions = goldSpent / actionCost;

                    Attributes.ACTION_POINTS.to(tank, Attributes.ACTION_POINTS.unsafeFrom(tank) + boughtActions);
                    Attributes.GOLD.to(tank, Attributes.GOLD.unsafeFrom(tank) - goldSpent);
                },
                new DiscreteIntegerRange("gold", new HashSet<>(IntStream.rangeClosed(1, maxBuys)
                        .map(n -> n * actionCost).boxed().collect(Collectors.toSet()))));
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetMoveRule(BaseAttribute<Integer> attribute,
            Integer cost) {
        return new PlayerActionRule<T>(
                PlayerRules.ActionKeys.MOVE,
                (s, t, n) -> !Attributes.DEAD.from(t).orElse(false) && attribute.from(t).orElse(0) >= cost
                        && canMoveTo(s, t.getPosition(), toType(n[0], Position.class)),
                (s, t, n) -> {
                    attribute.to(t, attribute.unsafeFrom(t) - cost);
                    s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                    t.setPosition(toType(n[0], Position.class));
                    s.getBoard().putUnit(t);
                },
                new MovePositionRange("target"));
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetUpgradeRangeRule(BaseAttribute<Integer> attribute,
            Integer cost) {
        return new PlayerActionRule<T>(
                PlayerRules.ActionKeys.UPGRADE_RANGE,
                (s, tank, n) -> {
                    return !Attributes.DEAD.from(tank).orElse(false) && (attribute.from(tank).orElse(0) >= cost)
                            && (Attributes.RANGE.in(tank));
                },
                (s, tank, n) -> {
                    Attributes.RANGE.to(tank, Attributes.RANGE.unsafeFrom(tank) + 1);
                    attribute.to(tank, attribute.unsafeFrom(tank) - cost);
                });
    }

    public static <T extends GenericTank> PlayerActionRule<T> GetShareGoldWithTaxRule(int taxAmount) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.DONATE,
                (s, tank, n) -> {
                    GenericTank other = toType(n[0], GenericTank.class);
                    int donation = toType(n[1], Integer.class);

                    return !Attributes.DEAD.from(tank).orElse(false)
                            && (Attributes.GOLD.from(tank).orElse(0) >= donation + taxAmount)
                            && Attributes.GOLD.in(other)
                            && getSpacesInRange(tank.getPosition(), Attributes.RANGE.from(tank).orElse(0))
                                    .contains(other.getPosition());
                },
                (s, tank, n) -> {
                    GenericTank other = toType(n[0], GenericTank.class);
                    int donation = toType(n[1], Integer.class);

                    Attributes.GOLD.to(tank, Attributes.GOLD.unsafeFrom(tank) - (donation + taxAmount));
                    Attributes.GOLD.to(other, Attributes.GOLD.unsafeFrom(other) + donation);
                    s.getCouncil().setCoffer(s.getCouncil().getCoffer() + 1);
                },
                new DonateTankRange("target"),
                new IntegerRange("donation"));
    }

    public static PlayerActionRule<Council> GetCofferCostStimulusRule(int cost) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.STIMULUS,
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    return !Attributes.DEAD.from(t).orElse(false) && Attributes.ACTION_POINTS.in(t)
                            && c.getCoffer() >= cost;
                },
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    Attributes.ACTION_POINTS.to(t, Attributes.ACTION_POINTS.unsafeFrom(t) + 1);
                    c.setCoffer(c.getCoffer() - cost);
                },
                new TankRange<Council>("target"));
    }

    public static PlayerActionRule<Council> GetRuleCofferCostGrantLife(int cost) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.GRANT_LIFE,
                (s, c, n) -> {
                    GenericTank t = toType(n[0], GenericTank.class);
                    return Attributes.DEAD.in(t) && Attributes.DURABILITY.in(t) && c.getCoffer() >= cost;
                },
                (s, c, n) -> {
                    c.setCoffer(c.getCoffer() - cost);
                    GenericTank t = toType(n[0], GenericTank.class);
                    if (Attributes.DEAD.unsafeFrom(t)) {
                        Attributes.DEAD.to(t, false);
                        Attributes.DURABILITY.to(t, 1);
                        s.getCouncil().getCouncillors().remove(t.getPlayer());
                    } else {
                        Attributes.DURABILITY.to(t, Attributes.DURABILITY.unsafeFrom(t) + 1);
                    }
                },
                new TankRange<Council>("target"));
    }

    public static <T extends GenericTank> PlayerActionRule<T> SpendActionToShootGeneric(
            ITriPredicate<State, Position, Position> lineOfSight,
            ITriConsumer<State, T, IUnit> handleHit) {
        return new PlayerActionRule<>(
                PlayerRules.ActionKeys.SHOOT,
                (s, t, n) -> {
                    return s.getBoard().isValidPosition(toType(n[0], Position.class))
                            && !Attributes.DEAD.from(t).orElse(false)
                            && (Attributes.ACTION_POINTS.from(t).orElse(0) >= 1)
                            && (t.getPosition().distanceFrom(toType(n[0], Position.class)) <= Attributes.RANGE.from(t)
                                    .orElse(0))
                            && lineOfSight.test(s, t.getPosition(), toType(n[0], Position.class));
                },
                (s, t, n) -> {
                    Position target = toType(n[0], Position.class);
                    boolean hit = toType(n[1], Boolean.class);
                    Attributes.ACTION_POINTS.to(t, Attributes.ACTION_POINTS.unsafeFrom(t) - 1);

                    Optional<IUnit> optionalUnit = s.getBoard().getUnit(target);
                    if (optionalUnit.isEmpty()) {
                        throw new Error(
                                String.format("Target position %s is not on the game board", target.toString()));
                    }
                    IUnit unit = optionalUnit.get();

                    if (hit) {
                        handleHit.accept(s, t, unit);
                    }
                },
                new ShootPositionRange("target", lineOfSight),
                new BooleanRange("hit"));
    }

    public static <T extends GenericTank> PlayerActionRule<T> SpendActionToShootWithDeathHandle(
            ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<State, T, GenericTank> handleDeath) {
        return SpendActionToShootGeneric(lineOfSight, (s, t, u) -> {
            switch (u) {
                case GenericTank tank -> {
                    Attributes.DURABILITY.to(tank, Attributes.DURABILITY.unsafeFrom(tank) - 1);
                    if (!Attributes.DEAD.unsafeFrom(tank) && Attributes.DURABILITY.unsafeFrom(tank) == 0) {
                        handleDeath.accept(s, t, tank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - 1);
                case EmptyUnit emptyUnit -> {
                    /* MISS */ }
                default -> throw new Error("Unhandled tank shot onto " + u.getClass().getName());
            }
        });
    }

    public static final PlayerActionRule<Tank> SHOOT_V3 = SpendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV3,
            (s, t, d) -> {
                t.setGold(t.getGold() + Attributes.GOLD.unsafeFrom(d) + Attributes.BOUNTY.unsafeFrom(d));
            });

    public static final PlayerActionRule<Tank> SHOOT_V4 = SpendActionToShootWithDeathHandle(
            LineOfSight::hasLineOfSightV4,
            (s, tank, dead) -> {
                tank.setGold(tank.getGold() + Attributes.BOUNTY.unsafeFrom(dead));
                switch (Attributes.GOLD.unsafeFrom(dead)) {
                    case 0 -> {
                    }
                    case 1 -> tank.setGold(tank.getGold() + 1);
                    default -> {
                        // Tax is target tank gold * 0.25 rounded up
                        int tax = (Attributes.GOLD.unsafeFrom(dead) + 2) / 4;
                        tank.setGold(tank.getGold() + Attributes.GOLD.unsafeFrom(dead) - tax);
                        s.getCouncil().setCoffer(s.getCouncil().getCoffer() + tax);
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