package pro.trevor.tankgame.rule.impl.version3;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.BaseRuleset;
import pro.trevor.tankgame.rule.impl.IRuleset;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.None;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.rule.definition.*;
import pro.trevor.tankgame.rule.definition.enforcer.EnforcerRuleset;
import pro.trevor.tankgame.rule.definition.enforcer.MaximumEnforcer;
import pro.trevor.tankgame.rule.definition.enforcer.MinimumEnforcer;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleset;
import pro.trevor.tankgame.rule.definition.player.PlayerSelfActionRule;
import pro.trevor.tankgame.util.Util;
import pro.trevor.tankgame.state.board.floor.AbstractPositionedFloor;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.IDurable;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.board.unit.Wall;
import pro.trevor.tankgame.state.meta.Council;

import java.util.*;

public class Ruleset extends BaseRuleset implements IRuleset {

    public static boolean councilCanBounty = true;

    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getDurability, Tank::setDurability, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getRange, Tank::setRange, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getGold, Tank::setGold, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getActions, Tank::setActions, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Tank::getActions, Tank::setActions, 5));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getBounty, Tank::setBounty, 0));
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
        tickRules.put(Tank.class, new TickActionRule<>((t, s) -> {
            if (!t.isDead()) {
                t.setActions(t.getActions() + 1);
                if (s.getBoard().getFloor(t.getPosition()).orElse(null) instanceof GoldMine) {
                    Set<Position> mines = new HashSet<>();
                    Util.findAllConnectedMines(mines, s, t.getPosition());
                    int tanks = (int) mines.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
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
        metaTickRules.put(Board.class, new MetaTickActionRule<>((b, s) -> {
            List<Position> mines = b.gatherFloors(GoldMine.class).stream()
                    .map(AbstractPositionedFloor::getPosition).toList();
            List<Set<Position>> allMines = new ArrayList<>();

            for (Position p : mines) {
                if (allMines.stream().flatMap(Collection::stream).anyMatch(p::equals)) {
                    continue;
                }
                Set<Position> thisMine = new HashSet<>();
                Util.findAllConnectedMines(thisMine, s, p);
                allMines.add(thisMine);
            }

            for (Set<Position> mine : allMines) {
                int tanks = (int) mine.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank tank && !tank.isDead()).count();
                int goldToGain = (tanks == 0) ? mine.size() : (mine.size() % tanks);
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
            }
        }));

        // Handle resetting the council's ability to apply a bounty
        metaTickRules.put(None.class, new MetaTickActionRule<>((n, s) -> {
            councilCanBounty = true;
            s.setTick(s.getTick() + 1);
        }));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();

        // Handle tank destruction
        conditionalRules.put(Tank.class, new ConditionalRule<>((t, s) -> t.getDurability() == 0, (t, s) -> {
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
        conditionalRules.put(Wall.class, new ConditionalRule<>((t, s) -> t.getDurability() == 0, (t, s) -> {
            s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
            if (Util.isOrthAdjToMine(s, t.getPosition())) {
                s.getBoard().putFloor(new GoldMine(t.getPosition()));
            }
        }));
    }

    @Override
    public void registerPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset playerRules = ruleset.getPlayerRules();

        playerRules.putSelfRule(Tank.class, Integer.class,
                new PlayerSelfActionRule<>(Rules.BUY_ACTION, (t, s) -> !t.isDead() && t.getGold() >= 3, (t, n, s) -> {
                    int gold = n.orElseThrow();
                    int n5 =  gold / 5;
                    int rem = gold - n5 * 5;
                    int n3 = gold / 3;
                    assert rem == n3 * 3;

                    t.setActions(t.getActions() + n5 * 2 + n3);
                    t.setGold(t.getGold() - gold);
                }));

        playerRules.putSelfRule(Tank.class, Void.class,
                new PlayerSelfActionRule<>(Rules.UPGRADE_RANGE, (t, s) -> !t.isDead() && t.getGold() >= 8, (t, n, s) -> {
                    t.setRange(t.getRange() + 1);
                    t.setGold(t.getGold() - 8);
                }));

        playerRules.put(Tank.class, Tank.class, Integer.class,
                new PlayerActionRule<>(Rules.DONATE, (t, u, s) ->
                        !t.isDead() && !u.isDead() && t.getGold() >= 2 && Util.getSpacesInRange(t.getPosition(), t.getRange()).contains(u.getPosition()),
                        (t, u, n, s) -> {
                            int donation = n.orElseThrow();
                            assert t.getGold() >= donation + 1;
                            t.setGold(t.getGold() - donation - 1);
                            u.setGold(u.getGold() + donation);
                        }));

        playerRules.put(Tank.class, Position.class, Void.class,
                new PlayerActionRule<>(Rules.MOVE, (t, p, s) ->
                        !t.isDead() && t.getActions() >= 1 && Util.canMoveTo(s, t.getPosition(), p),
                        (t, p, n, s) -> {
                    t.setActions(t.getActions() - 1);
                    s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                    t.setPosition(p);
                    s.getBoard().putUnit(t);
                }));

        playerRules.put(Tank.class, Position.class, Boolean.class, new PlayerActionRule<>(Rules.SHOOT, (t, p, s) ->
                !t.isDead() && t.getActions() >= 1 && t.getPosition().distanceFrom(p) <= t.getRange()
                        && LineOfSight.hasLineOfSight(s, t.getPosition(), p),
                (t, p, n, s) -> {
                    if (s.getBoard().getUnit(p).orElse(null) instanceof IDurable unit) {
                        t.setActions(t.getActions() - 1);
                        if (unit instanceof Tank tank) {
                            if (tank.isDead()) {
                                tank.setDurability(tank.getDurability() - 1);
                            } else {
                                if (n.orElseThrow()) {
                                    tank.setDurability(tank.getDurability() - 1);
                                    if (tank.getDurability() == 0) {
                                        t.setGold(t.getGold() + tank.getGold() + tank.getBounty());
                                        tank.setBounty(0);
                                        tank.setGold(0);
                                    }
                                }
                            }
                        } else if (unit instanceof Wall wall) {
                            wall.setDurability(wall.getDurability() - 1);
                        } else if (unit instanceof EmptyUnit) {
                            // MISS
                        } else {
                            throw new Error("Unhandled tank shot onto " + unit.getClass().getName());
                        }
                    }
        }));
    }

    @Override
    public void registerMetaPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset metaPlayerRules = ruleset.getMetaPlayerRules();

        metaPlayerRules.put(Council.class, Tank.class, Void.class, new PlayerActionRule<>(Rules.STIMULUS,
                (c, t, s) -> c.getCoffer() >= 3,
                (c, t, n, s) -> {
                    t.setActions(t.getActions() + 1);
                    c.setCoffer(c.getCoffer() - 3);
        }));

        metaPlayerRules.put(Council.class, Tank.class, Void.class, new PlayerActionRule<>(Rules.GRANT_LIFE,
                (c, t, s) -> c.getCoffer() >= 15,
                (c, t, n, s) -> {
                    t.setDurability(t.getDurability() + 1);
                    c.setCoffer(c.getCoffer() - 15);
        }));

        metaPlayerRules.put(Council.class, Tank.class, Integer.class, new PlayerActionRule<>(Rules.BOUNTY,
                (c, t, s) -> councilCanBounty,
                (c, t, n, s) -> {
                    int bounty = n.orElseThrow();
                    assert c.getCoffer() >= bounty;
                    t.setBounty(t.getBounty() + bounty);
                    c.setCoffer(c.getCoffer() - bounty);
                    councilCanBounty = false;
                }));
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
