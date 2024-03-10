package rule.impl;

import rule.definition.*;
import rule.definition.enforcer.EnforcerRuleset;
import rule.definition.enforcer.MaximumEnforcer;
import rule.definition.enforcer.MinimumEnforcer;
import rule.definition.player.PlayerActionRule;
import rule.definition.player.PlayerRuleset;
import rule.definition.player.PlayerSelfActionRule;
import state.board.Board;
import state.board.Position;
import state.board.Util;
import state.board.floor.AbstractPositionedFloor;
import state.board.floor.GoldMine;
import state.board.unit.EmptyUnit;
import state.board.unit.IDurable;
import state.board.unit.Tank;
import state.board.unit.Wall;
import state.meta.Council;
import state.meta.Player;

import java.util.*;

import static util.LineOfSight.hasLineOfSight;

public class Version3 extends BaseRuleset implements IRuleset {

    @Override
    public void registerEnforcerRules(RulesetDescription ruleset) {
        EnforcerRuleset invariants = ruleset.getEnforcerRules();

        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getDurability, Tank::setDurability, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getRange, Tank::setRange, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getGold, Tank::setGold, 0));
        invariants.put(Tank.class, new MinimumEnforcer<>(Tank::getActions, Tank::setActions, 0));
        invariants.put(Tank.class, new MaximumEnforcer<>(Tank::getActions, Tank::setActions, 5));
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
                    int tanks = (int) mines.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank).count();
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
                int tanks = (int) mine.stream().filter((p) -> s.getBoard().getUnit(p).orElse(null) instanceof Tank).count();
                int goldToGain = mine.size() % tanks;
                s.getCouncil().setCoffer(s.getCouncil().getCoffer() + goldToGain);
            }
        }));
    }

    @Override
    public void registerConditionalRules(RulesetDescription ruleset) {
        ApplicableRuleset conditionalRules = ruleset.getConditionalRules();

        // Handle tank destruction
        conditionalRules.put(Tank.class, new ConditionalRule<>((t, s) -> t.getDurability() == 0, (t, s) -> {
            if (t.isDead()) {
                s.getBoard().putUnit(new EmptyUnit(t.getPosition()));
                Player tankPlayer = t.getPlayers()[0];
                s.getCouncil().getCouncillors().remove(tankPlayer);
                s.getCouncil().getSenators().add(tankPlayer);
            } else {
                t.setDead(true);
                t.setActions(0);
                t.setGold(0);
                t.setDurability(3);
                s.getCouncil().getCouncillors().add(t.getPlayers()[0]);
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

        // Buy 1 action
        playerRules.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((t, s) -> !t.isDead() && t.getGold() >= 3, (t, s) -> {
                    t.setActions(t.getActions() + 1);
                    t.setGold(t.getGold() - 3);
                }));

        // Buy 2 actions
        playerRules.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((t, s) -> !t.isDead() && t.getGold() >= 5, (t, s) -> {
                    t.setActions(t.getActions() + 2);
                    t.setGold(t.getGold() - 5);
                }));

        // Upgrade range
        playerRules.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((t, s) -> !t.isDead() && t.getGold() >= 8, (t, s) -> {
                    t.setRange(t.getRange() + 1);
                    t.setGold(t.getGold() - 8);
                }));

        // Shoot at a position
        playerRules.put(Tank.class, Position.class, new PlayerActionRule<>((t, p, s) ->
                !t.isDead() && t.getActions() >= 1 && t.getPosition().distanceFrom(p) <= t.getRange()
                        && s.getBoard().getUnit(p).orElse(null) instanceof IDurable
                        && hasLineOfSight(s, t.getPosition(), p),
                (t, y, s) -> {
                    if (s.getBoard().getUnit(y).orElse(null) instanceof IDurable unit) {
                        t.setActions(t.getActions() - 1);
                        if (unit instanceof Tank tank) {
                            if (tank.isDead()) {
                                tank.setDurability(tank.getDurability() - 1);
                            } else {
                                boolean hit = false;
                                Random random = new Random(System.currentTimeMillis());
                                for (int i = t.getPosition().distanceFrom(y); i <= t.getRange(); ++i) {
                                    if (random.nextBoolean()) {
                                        hit = true;
                                        break;
                                    }
                                }
                                if (hit) {
                                    tank.setDurability(tank.getDurability() - 1);
                                    if (tank.getDurability() == 0) {
                                        t.setGold(t.getGold() + tank.getGold());
                                        tank.setGold(0);
                                    }
                                }
                            }
                        } else if (unit instanceof Wall wall) {
                            wall.setDurability(wall.getDurability() - 1);
                        } else {
                            throw new Error("Unhandled tank shot onto " + unit.getClass().getName());
                        }
                    }
        }));
    }

    @Override
    public void registerMetaPlayerRules(RulesetDescription ruleset) {
        PlayerRuleset metaPlayerRules = ruleset.getMetaPlayerRules();

        // Action stimulus
        metaPlayerRules.put(Council.class, Tank.class, new PlayerActionRule<>((c, t, s) -> c.getCoffer() >= 3,
                (c, t, s) -> {
                    t.setActions(t.getActions() + 1);
                    c.setCoffer(c.getCoffer() - 3);
        }));

        // Grant life
        metaPlayerRules.put(Council.class, Tank.class, new PlayerActionRule<>((c, t, s) -> c.getCoffer() >= 15,
                (c, t, s) -> {
                    t.setDurability(t.getDurability() + 1);
                    c.setCoffer(c.getCoffer() - 15);
        }));

        // TODO implement bounties
    }
}
