package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.util.BaseRuleset;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.None;
import pro.trevor.tankgame.rule.impl.version3.range.DonateTankRange;
import pro.trevor.tankgame.rule.impl.version3.range.MovePositionRange;
import pro.trevor.tankgame.rule.impl.version3.range.ShootPositionRange;
import pro.trevor.tankgame.rule.impl.version3.range.TankRange;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.rule.definition.*;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.state.board.floor.AbstractPositionedFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.range.BooleanRange;
import pro.trevor.tankgame.util.range.DiscreteIntegerRange;
import pro.trevor.tankgame.util.range.IntegerRange;

import java.util.*;

import static pro.trevor.tankgame.util.Util.*;

public class Ruleset extends BaseRuleset implements IRuleset {

    public static boolean councilCanBounty = true;

    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank3.class, new MinimumEnforcer<>(Tank3::getDurability, Tank3::setDurability, 0));
        invariants.put(Tank3.class, new MinimumEnforcer<>(Tank3::getRange, Tank3::setRange, 0));
        invariants.put(Tank3.class, new MinimumEnforcer<>(Tank3::getGold, Tank3::setGold, 0));
        invariants.put(Tank3.class, new MinimumEnforcer<>(Tank3::getActions, Tank3::setActions, 0));
        invariants.put(Tank3.class, new MaximumEnforcer<>(Tank3::getActions, Tank3::setActions, 5));
        invariants.put(Tank3.class, new MinimumEnforcer<>(Tank3::getBounty, Tank3::setBounty, 0));
        invariants.put(Wall.class, new MinimumEnforcer<>(Wall::getDurability, Wall::setDurability, 0));
    }

    @Override
    public void registerMetaEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getMetaEnforcerRules();

        invariants.put(Council.class, new MinimumEnforcer<>(Council::getCoffer, Council::setCoffer, 0));
    }

    @Override
    public void registerTickRules(RulesetDescription ruleset) {
        ApplicableRuleset tickRules = ruleset.getTickRules();

        // Handle gold mine distribution to tanks
        tickRules.put(Tank3.class, new TickActionRule<>((s, t) -> {
            if (!t.isDead()) {
                t.setActions(t.getActions() + 1);
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                    Set<Position> mines = new HashSet<>();
                    findAllConnectedMines(mines, s, t.getPosition());
                    int tanks = (int) mines.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank3 tank && !tank.isDead()).count();
                    int goldToGain = mines.size() / tanks;
                    t.setGold(t.getGold() + goldToGain);
                }
            }
        }));
    }

    @Override
    public void registerMetaTickRules(RulesetDescription ruleset) {
        ApplicableRuleset metaTickRules = ruleset.getMetaTickRules();

        // Handle gold mine distribution to coffer
        metaTickRules.put(Board.class, new MetaTickActionRule<>((s, b) -> {
            List<Position> mines = b.gatherFloors(GoldMine.class).stream()
                    .map(AbstractPositionedFloor::getPosition).toList();
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
                int tanks = (int) mine.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank3 tank && !tank.isDead()).count();
                int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
            }
        }));

        // Handle resetting the council's ability to apply a bounty
        metaTickRules.put(None.class, new MetaTickActionRule<>((s, n) -> {
            councilCanBounty = true;
            s.setTick(s.getTick() + 1);
        }));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();

        // Handle tank destruction
        conditionalRules.put(Tank3.class, new ConditionalRule<>((s, t) -> t.getDurability() == 0, (s, t) -> {
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
        }));

        // Handle wall destruction
        conditionalRules.put(Wall.class, new ConditionalRule<>((s, t) -> t.getDurability() == 0, (s, t) -> {
            s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
            if (isOrthAdjToMine(s, t.getPosition())) {
                s.getBoard().putFloor(new GoldMine(t.getPosition()));
            }
        }));
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.put(Tank3.class, new PlayerActionRule<>(Rules.BUY_ACTION,
            (s, t, n) -> !t.isDead() && t.getGold() >= 3,
            (s, t, n) -> {
                int gold = toType(n[0], Integer.class);
                int n5 = gold / 5;
                int rem = gold - n5 * 5;
                int n3 = gold / 3;
                assert rem == n3 * 3;

                t.setActions(t.getActions() + n5 * 2 + n3);
                t.setGold(t.getGold() - gold);
            }, new DiscreteIntegerRange("gold",new HashSet<>(List.of(3, 5, 8, 10))))
        );

        playerRules.put(Tank3.class, new PlayerActionRule<>(Rules.UPGRADE_RANGE,
            (s, t, n) -> !t.isDead() && t.getGold() >= 8,
            (s, t, n) -> {
                t.setRange(t.getRange() + 1);
                t.setGold(t.getGold() - 8);
            })
        );

        playerRules.put(Tank3.class, new PlayerActionRule<>(Rules.DONATE,
            (s, t, n) -> {
                Tank3 other = toType(n[0], Tank3.class);
                int donation = toType(n[1], Integer.class);
                return !t.isDead() && !other.isDead() && t.getGold() >= donation + 1 &&
                        getSpacesInRange(t.getPosition(), t.getRange()).contains(other.getPosition());
            }, (s, t, n) -> {
            Tank3 other = toType(n[0], Tank3.class);
                int donation = toType(n[1], Integer.class);
                assert t.getGold() >= donation + 1;
                t.setGold(t.getGold() - donation - 1);
                other.setGold(other.getGold() + donation);
            }, new DonateTankRange("target"), new IntegerRange("donation"))
        );

        playerRules.put(Tank3.class, new PlayerActionRule<>(Rules.MOVE,
            (s, t, n) -> !t.isDead() && t.getActions() >= 1 && canMoveTo(s, t.getPosition(), toType(n[0], Position.class)),
            (s, t, n) -> {
                t.setActions(t.getActions() - 1);
                s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                t.setPosition(toType(n[0], Position.class));
                s.getBoard().putUnit(t);
            }, new MovePositionRange("target"))
        );

        playerRules.put(Tank3.class, new PlayerActionRule<>(Rules.SHOOT,
            (s, t, n) -> !t.isDead() && t.getActions() >= 1 &&
                t.getPosition().distanceFrom(toType(n[0], Position.class)) <= t.getRange() &&
                LineOfSight.hasLineOfSightV3(s, t.getPosition(), toType(n[0], Position.class)),
            (s, t, n) -> {
                t.setActions(t.getActions() - 1);
                Optional<IUnit> optionalUnit = s.getBoard().getUnit(toType(n[0], Position.class));
                if (optionalUnit.isEmpty()) {
                    throw new Error("Attempted to shoot onto invalid space");
                }
                IUnit unit = optionalUnit.get();
                switch (unit) {
                    case Tank3 tank -> {
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
            }, new ShootPositionRange("target"), new BooleanRange("hit"))
        );
    }

    @Override
    public void registerMetaPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset metaPlayerRules = ruleset.getMetaPlayerRules();

        metaPlayerRules.put(Council.class, new PlayerActionRule<>(Rules.STIMULUS,
                (s, c, n) -> {
                    Tank3 t = toType(n[0], Tank3.class);
                    return !t.isDead() && c.getCoffer() >= 3;
                },
                (s, c, n) -> {
                    Tank3 t = toType(n[0], Tank3.class);
                    t.setActions(t.getActions() + 1);
                    c.setCoffer(c.getCoffer() - 3);
                }, new TankRange<Council>("target"))
        );

        metaPlayerRules.put(Council.class, new PlayerActionRule<>(Rules.GRANT_LIFE,
                (s, c, n) -> c.getCoffer() >= 15,
                (s, c, n) -> {
                    Tank3 t = toType(n[0], Tank3.class);
                    t.setDurability(t.getDurability() + 1);
                    c.setCoffer(c.getCoffer() - 15);
                }, new TankRange<Council>("target"))
        );

        metaPlayerRules.put(Council.class, new PlayerActionRule<>(Rules.BOUNTY,
                (s, c, n) -> {
                    Tank3 t = toType(n[0], Tank3.class);
                    return !t.isDead() && councilCanBounty;
                },
                (s, c, n) -> {
                    Tank3 t = toType(n[0], Tank3.class);
                    int bounty = toType(n[1], Integer.class);
                    assert c.getCoffer() >= bounty;
                    t.setBounty(t.getBounty() + bounty);
                    c.setCoffer(c.getCoffer() - bounty);
                    councilCanBounty = false;
                }, new TankRange<Council>("target"), new DiscreteIntegerRange("bounty", 1, 5))
        );
    }

    public static class Rules {

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
