package pro.trevor.tankgame.rule.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.MetaTickActionRule;
import pro.trevor.tankgame.rule.definition.TickActionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.AbstractPositionedFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.range.DonateTankRange;
import pro.trevor.tankgame.state.range.MovePositionRange;
import pro.trevor.tankgame.state.range.TankRange;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;
import pro.trevor.tankgame.util.range.IntegerRange;

import static pro.trevor.tankgame.util.Util.*;

public class Rules 
{
    public static class Conditional 
    {
        public static ConditionalRule<Wall> DESTROY_WALL_ON_ZERO_DURABILITY = new ConditionalRule<>((s, w) -> w.getDurability() == 0, 
                                                                                                    (s, w) -> {
            s.getBoard().putUnit(new EmptyUnit(w.getPosition()));
            if (isOrthAdjToMine(s, w.getPosition())) {
                s.getBoard().putFloor(new GoldMine(w.getPosition()));
            }
        });

        public static ConditionalRule<Tank> KILL_OR_DESTROY_TANK_ON_ZERO_DURABILITY = new ConditionalRule<>((s, t) -> t.getDurability() == 0, 
                                                                                                            (s, t) -> {
            if (t.isDead()) {
                s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                String tankPlayer = t.getPlayer();
                s.getCouncil().getCouncillors().remove(tankPlayer);
                s.getCouncil().getSenators().add(tankPlayer);
            } else {
                t.setDead(true);
                t.setActions(0);
                t.setGold(0);
                t.setDurability(3);
                s.getCouncil().getCouncillors().add(t.getPlayer());
            }
        });
    }

    public static class TickAction
    {
        public static TickActionRule<Tank> DISTRIBUTE_GOLD_TO_TANKS_RULE = new TickActionRule<>((s, t) -> {
            if (!t.isDead()) {
                t.setActions(t.getActions() + 1);
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                    Set<Position> mines = new HashSet<>();
                    findAllConnectedMines(mines, s, t.getPosition());
                    int tanks = (int) mines.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
                    int goldToGain = mines.size() / tanks;
                    t.setGold(t.getGold() + goldToGain);
                }
            }
        });
    }

    public static class MetaTickAction
    {
        public static MetaTickActionRule<Board> GOLD_MINE_REMAINDER_GOES_TO_COFFER = new MetaTickActionRule<>((s, b) -> {
            List<Position> mines = b.gatherFloors(GoldMine.class).stream().map(AbstractPositionedFloor::getPosition).toList();
            List<Set<Position>> allMines = new ArrayList<>();
    
            for (Position p : mines) {
                if (allMines.stream().flatMap(Collection::stream).anyMatch(p::equals)) {
                    continue;
                }
                Set<Position> thisMine = new HashSet<>();
                findAllConnectedMines(thisMine, s, p);
                allMines.add(thisMine);
            }
    
            for (Set<Position> mine : allMines) {
                int tanks = (int) mine.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
                int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
            }
        });
    }

    public static class PlayerAction 
    {
        public static PlayerActionRule<Tank> BUY_ACTION_WITH_GOLD_PLUS_DISCOUNT = new PlayerActionRule<>(
            Rules.PlayerAction.Keys.BUY_ACTION,
            (s, t, n) -> !t.isDead() && t.getGold() >= 3,
            (s, t, n) -> {
                int gold = toType(n[0], Integer.class);
                int n5 = gold / 5;
                int rem = gold - n5 * 5;
                int n3 = gold / 3;
                assert rem == n3 * 3;

                t.setActions(t.getActions() + n5 * 2 + n3);
                t.setGold(t.getGold() - gold);
            },
            new DiscreteIntegerRange("gold",new HashSet<>(List.of(3, 5, 8, 10)))
        );

        public static PlayerActionRule<Tank> SPEND_ACTION_TO_MOVE = new PlayerActionRule<>(
            Rules.PlayerAction.Keys.MOVE,
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
                Rules.PlayerAction.Keys.UPGRADE_RANGE,
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
                Rules.PlayerAction.Keys.DONATE,
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
                }, 
                new DonateTankRange("target"), 
                new IntegerRange("donation")
            );
        }

        public static PlayerActionRule<Council> GetCofferCostStimulusRule(int cost)
        {
            return new PlayerActionRule<>(Keys.STIMULUS,
                (s, c, n) -> {
                    Tank t = toType(n[0], Tank.class);
                    return !t.isDead() && c.getCoffer() >= cost;
                },
                (s, c, n) -> {
                    Tank t = toType(n[0], Tank.class);
                    t.setActions(t.getActions() + 1);
                    c.setCoffer(c.getCoffer() - cost);
                }, 
                new TankRange<Council>("target")
            );
        }

        public static PlayerActionRule<Council> GetRule_CofferCost_GrantLife(int cost)
        {
            return new PlayerActionRule<>(Keys.GRANT_LIFE,
                (s, c, n) -> c.getCoffer() >= cost,
                (s, c, n) -> {
                    Tank t = toType(n[0], Tank.class);
                    t.setDurability(t.getDurability() + 1);
                    c.setCoffer(c.getCoffer() - cost);
                },
                new TankRange<Council>("target")
            );
        }

        public static class Keys {

            public static final String SHOOT = "shoot";
            public static final String MOVE = "move";
            public static final String DONATE = "donate";
            public static final String BUY_ACTION = "buy_action";
            public static final String UPGRADE_RANGE = "upgrade_range";
    
            public static final String STIMULUS = "stimulus";
            public static final String GRANT_LIFE = "grant_life";
            public static final String BOUNTY = "bounty";
        }

        public static PlayerActionRule<Tank> SPEND_ACTION_TO_SHOOT_LOSv3 = new PlayerActionRule<Tank>(
            Rules.PlayerAction.Keys.SHOOT,
            (s, t, n) -> (!t.isDead() && t.getActions() >= 1) && (t.getPosition().distanceFrom(toType(n[0], Position.class)) <= t.getRange()) && (LineOfSight.hasLineOfSightV3(s, t.getPosition(), toType(n[0], Position.class))),
            (s, t, n) -> {
                if (s.getBoard().getUnit(toType(n[0], Position.class)).orElse(null) instanceof IDurable unit) {
                    t.setActions(t.getActions() - 1);
                    switch (unit) {
                        case Tank tank -> {
                            if (tank.isDead()) {
                                tank.setDurability(tank.getDurability() - 1);
                            } else {
                                if (toType(n[1], Boolean.class)) {
                                    tank.setDurability(tank.getDurability() - 1);
                                    if (tank.getDurability() == 0) {
                                        t.setGold(t.getGold() + tank.getGold() + tank.getBounty());
                                        tank.setBounty(0);
                                        tank.setGold(0);
                                    }
                                }
                            }
                        }
                        case Wall wall -> wall.setDurability(wall.getDurability() - 1);
                        case EmptyUnit emptyUnit -> { /* MISS */ }
                        default -> throw new Error("Unhandled tank shot onto " + unit.getClass().getName());
                    }
                }
            }, 
            new ShootPositionRange("target"), 
            new BooleanRange("hit")
        );
    }
}