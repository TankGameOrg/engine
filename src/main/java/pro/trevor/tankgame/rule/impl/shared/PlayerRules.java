package pro.trevor.tankgame.rule.impl.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.rule.impl.version3.range.*;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.util.function.ITriPredicate;
import pro.trevor.tankgame.util.range.BooleanRange;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;
import pro.trevor.tankgame.util.range.IntegerRange;

import static pro.trevor.tankgame.util.Util.*;

public class PlayerRules 
{
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
        new DiscreteIntegerRange("gold",new HashSet<>(List.of(3, 5, 8, 10)))
    );

    public static PlayerActionRule<Tank> BuyActionWithGold(int actionCost)
    {
        if (actionCost <= 0)
            throw new Error("Illegal Action Cost of " + actionCost + " gold. Must be positive and non-zero.");

        return new PlayerActionRule<>(
            ActionKeys.BUY_ACTION,
            (s, t, n) -> {
                int goldSpent = toType(n[0], Integer.class);
                return !t.isDead() && (t.getGold() >= goldSpent) && (goldSpent >= actionCost) && (goldSpent % actionCost == 0);
            }, 
            (s, t, n) -> {
                int attemptedGoldSpent = toType(n[0], Integer.class);
                int boughtActions = attemptedGoldSpent / actionCost;
                int actualGoldSpent = boughtActions * actionCost;
                assert attemptedGoldSpent == actualGoldSpent;
                
                t.setActions(t.getActions() + boughtActions);
                t.setGold(t.getGold() - actualGoldSpent);
            }, 
            new DiscreteIntegerRange("gold", IntStream.rangeClosed(1, 5).map(n -> n * actionCost).boxed().collect(Collectors.toSet()))
        );
    }

    public static final PlayerActionRule<Tank> SPEND_ACTION_TO_MOVE = new PlayerActionRule<>(
        PlayerRules.ActionKeys.MOVE,
        (s, t, n) -> !t.isDead() && t.getActions() >= 1 && canMoveTo(s, t.getPosition(), toType(n[0], Position.class)),
        (s, t, n) -> {
            t.setActions(t.getActions() - 1);
            s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
            t.setPosition(toType(n[0], Position.class));
            s.getBoard().putUnit(t);
        }, 
        new MovePositionRange("target")
    );

    public static PlayerActionRule<Tank> GetUpgradeRangeWithGoldRule(int cost)
    {
        return new PlayerActionRule<>(
            PlayerRules.ActionKeys.UPGRADE_RANGE,
            (s, t, n) -> !t.isDead() && t.getGold() >= cost,
            (s, t, n) -> {
                t.setRange(t.getRange() + 1);
                t.setGold(t.getGold() - cost);
            }
        );
    }

    public static PlayerActionRule<Tank> GetShareGoldWithTaxRule(int taxAmount)
    {
        return new PlayerActionRule<>(
            PlayerRules.ActionKeys.DONATE,
            (s, t, n) -> {
                Tank other = toType(n[0], Tank.class);
                int donation = toType(n[1], Integer.class);
                return !t.isDead() && !other.isDead() && (t.getGold() >= donation + taxAmount) && getSpacesInRange(t.getPosition(), t.getRange()).contains(other.getPosition());
            }, 
            (s, t, n) -> {
                Tank other = toType(n[0], Tank.class);
                int donation = toType(n[1], Integer.class);
                assert t.getGold() >= donation + taxAmount;
                t.setGold(t.getGold() - donation - taxAmount);
                other.setGold(other.getGold() + donation);
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + 1);
            }, 
            new DonateTankRange("target"),
            new IntegerRange("donation")
        );
    }

    public static PlayerActionRule<Council> GetCofferCostStimulusRule(int cost)
    {
        return new PlayerActionRule<>(
            PlayerRules.ActionKeys.STIMULUS,
            (s, c, n) -> {
                Tank t = toType(n[0], Tank.class);
                return !t.isDead() && c.getCoffer() >= cost;
            },
            (s, c, n) -> {
                Tank t = toType(n[0], Tank.class);
                t.setActions(t.getActions() + 1);
                c.setCoffer(c.getCoffer() - cost);
            }, 
            new LivingTankRange<Council>("target")
        );
    }

    public static PlayerActionRule<Council> GetRuleCofferCostGrantLife(int cost)
    {
        return new PlayerActionRule<>(
            PlayerRules.ActionKeys.GRANT_LIFE,
            (s, c, n) -> c.getCoffer() >= cost,
            (s, c, n) -> {
                c.setCoffer(c.getCoffer() - cost);
                Tank t = toType(n[0], Tank.class);
                if (t.isDead()) {
                    t.setDead(false);
                    t.setDurability(1);
                    s.getCouncil().getCouncillors().remove(t.getPlayer());
                } else {
                    t.setDurability(t.getDurability() + 1);
                }
            },
            new TankRange<Council>("target")
        );
    }

    public static PlayerActionRule<Tank> SpendActionToShootGeneric(ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<State, Tank, IUnit> handleHit) {
        return new PlayerActionRule<>(PlayerRules.ActionKeys.SHOOT,
            (s, t, n) -> !t.isDead() && (t.getActions() >= 1) && (t.getPosition().distanceFrom(toType(n[0], Position.class)) <= t.getRange()) && lineOfSight.test(s, t.getPosition(), toType(n[0], Position.class)),
            (s, t, n) -> {
                Position target = toType(n[0], Position.class);
                boolean hit = toType(n[1], Boolean.class);
                t.setActions(t.getActions() - 1);

                Optional<IUnit> optionalUnit = s.getBoard().getUnit(target);
                if (optionalUnit.isEmpty()) {
                    throw new Error(String.format("Target position %s is not on the game board", target.toString()));
                }
                IUnit unit = optionalUnit.get();

                if (hit) {
                    handleHit.accept(s, t, unit);
                }
            },
            new ShootPositionRange("target", lineOfSight),
            new BooleanRange("hit")
        );
    }

    public static PlayerActionRule<Tank> SpendActionToShootWithDeathHandle(ITriPredicate<State, Position, Position> lineOfSight, ITriConsumer<State, Tank, Tank> handleDeath) {
        return SpendActionToShootGeneric(lineOfSight, (s, t, u) -> {
            switch (u) {
                case Tank tank -> {
                    tank.setDurability(tank.getDurability() - 1);
                    if (!tank.isDead() && tank.getDurability() == 0) {
                        handleDeath.accept(s, t, tank);
                    }
                }
                case BasicWall wall -> wall.setDurability(wall.getDurability() - 1);
                case EmptyUnit emptyUnit -> { /* MISS */ }
                default -> throw new Error("Unhandled tank shot onto " + u.getClass().getName());
            }
        });
    }

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