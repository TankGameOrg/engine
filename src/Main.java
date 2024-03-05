import rule.impl.*;
import rule.impl.enforcer.EnforcerRuleset;
import rule.impl.enforcer.MaximumEnforcer;
import rule.impl.enforcer.MinimumEnforcer;
import rule.impl.player.IPlayerRule;
import rule.impl.player.PlayerActionRule;
import rule.impl.player.PlayerRuleset;
import rule.impl.player.PlayerSelfActionRule;
import state.board.Position;
import state.State;
import state.board.Util;
import state.board.floor.GoldMine;
import state.board.unit.*;

import java.util.*;

public class Main {

    private static boolean isOrthAdjToGoldMine(State state, Position p) {
        for (Position x : Util.orthogonallyAdjacentPositions(p)) {
            if (state.getBoard().getFloor(x).orElse(null) instanceof GoldMine) {
                return true;
            }
        }
        return false;
    }

    private static void findAllConnectedMines(Set<Position> positions, State state, Position p) {
        if (!positions.contains(p)) {
            positions.add(p);
            Arrays.stream(Util.orthogonallyAdjacentPositions(p))
                    .filter((x) -> state.getBoard().getFloor(x).orElse(null) instanceof GoldMine)
                    .forEach((x) -> findAllConnectedMines(positions, state, x));
        }
    }

    private static Set<Position> getSpacesInRange(Position p, int range){
        Set<Position> output = new HashSet<>();
        for (int i = 0; i <= range; ++i) {
            for (int j = 0; j <= range; ++j) {
                output.add(new Position(p.x() + i, p.y() + j));
                output.add(new Position(p.x() - i, p.y() - j));
                output.add(new Position(p.x() - i, p.y() + j));
                output.add(new Position(p.x() + i, p.y() - j));
            }
        }
        return output;
    }

    private static boolean pointsSeparated(int a, int b, int c, Position p1, Position p2) {
        int fx1 = a * p1.x() + b * p1.y() - c;
        int fx2 = a * p2.x() + b * p2.y() - c;

        System.out.printf("%s %s %d %d\n", p1, p2, fx1, fx2);

        // points are separated by the line if the signs are opposite, or points hit a corner if fx1 == fx2
        return (fx1 * fx2) < 0 || fx1 == fx2;
    }

    private static boolean lineOfSight(State s, Position p1, Position p2) {
        int minx = Math.min(p1.x(), p2.x());
        int maxx = Math.max(p1.x(), p2.x());
        int miny = Math.min(p1.y(), p2.y());
        int maxy = Math.max(p1.y(), p2.y());

        int dx = p1.x() - p2.x();
        int dy = p1.y() - p2.y();

        Set<Position> points = new HashSet<>();

        // handle undefined slope
        if (dx == 0) {
            for (int y = miny + 1; y < maxy; ++y) {
                // we can use minx here since minx == maxx
                points.add(new Position(minx, y));
            }
        } else if (dy == 0) {
            for (int x = minx + 1; x < maxx; ++x) {
                // we can use miny here since miny == maxy
                points.add(new Position(x, miny));
            }
        } else {
            float slope = dx / (float) dy;

            // y - p1.y() = slope * (x - p1.x())
            // y - (dy/dx) * x - p1.y() + (dy/dx) * p1.x() = 0
            // dx*y - dy*x - dx*p1.y() + dy*p1.x() = 0
            // dy*x - dx*y + (dy*p1.x() - dx*p1.y()) = 0
            // Ax + By + C = 0
            int a = dy;
            int b = -dx;
            int c = dx * p1.y() - dy * p1.x();


            for (int x = minx; x <= maxx; ++x) {
                for (int y = miny; y <= maxy; ++y) {
                    // p1|p2 are eiter A|C or B|D
                    // A--B
                    // |  |
                    // D--C
                    // A is +0,+0
                    // B is +1,+0
                    // C is +1,+1
                    // D is +0,+1
                    // use A|C if slope is positive or zero
                    // use B|D if the slope is negative
                    Position q1, q2;
                    if (slope >= 0) {
                        q1 = new Position(x, y);
                        q2 = new Position(x + 1, y + 1);
                    } else {
                        q1 = new Position(x + 1, y);
                        q2 = new Position(x, y + 1);
                    }
                    if (pointsSeparated(a, b, c, q1, q2)) {
                        points.add(new Position(x, y));
                    }
                }
            }
        }

        points.remove(p1);
        points.remove(p2);

        System.out.println(points);

        for (Position p : points) {
            if (!(s.getBoard().getUnit(p).orElse(null) instanceof IWalkable)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        EnforcerRuleset unitInvariants = new EnforcerRuleset();

        unitInvariants.put(Tank.class, new MinimumEnforcer<>(Tank::getDurability, Tank::setDurability, 0));
        unitInvariants.put(Tank.class, new MinimumEnforcer<>(Tank::getRange, Tank::setRange, 0));
        unitInvariants.put(Tank.class, new MinimumEnforcer<>(Tank::getGold, Tank::setGold, 0));
        unitInvariants.put(Tank.class, new MinimumEnforcer<>(Tank::getActions, Tank::setActions, 0));
        unitInvariants.put(Tank.class, new MaximumEnforcer<>(Tank::getActions, Tank::setActions, 5));
        unitInvariants.put(Wall.class, new MinimumEnforcer<>(Wall::getDurability, Wall::setDurability, 0));


        State s = new State(11, 11);
        Tank t = new Tank(new Position(0, 0), 3, 0, 3, 2);
        s.getBoard().putUnit(new Tank(new Position(0, 1), 3, 0, 3, 2));
        s.getBoard().putFloor(new GoldMine(new Position(0, 0)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 1)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 2)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 3)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 4)));
        s.getBoard().putFloor(new GoldMine(new Position(0, 5)));


        // Test IEnforceable
        s.getBoard().putUnit(t);
        t.setDurability(-1);
        System.out.println(t.toInfoString());
        unitInvariants.enforceRules(s, t);
        System.out.println(t.toInfoString());


        // Test TickActionRule
        ApplicableRuleset tickRules = new ApplicableRuleset();
        tickRules.put(Tank.class, new TickActionRule<>((x, y) -> {
            System.out.println("Tick rules work");
            if (!x.isDead()) {
                x.setActions(x.getActions() + 1);
                if (y.getBoard().getFloor(x.getPosition()).orElse(null) instanceof GoldMine) {
                    Set<Position> mines = new HashSet<>();
                    findAllConnectedMines(mines, y, x.getPosition());
                    int tanks = (int) mines.stream().filter((a) -> y.getBoard().getUnit(a).orElse(null) instanceof Tank).count();
                    int goldToGain = mines.size() / tanks;
                    x.setGold(x.getGold() + goldToGain);
                }
            }
        }));

        for (Tank tank : s.getBoard().gatherUnits(Tank.class)) {
            tickRules.applyRules(s, tank);
        }

        System.out.println(t.toInfoString());

        // Test IConditionalRule
        ApplicableRuleset conditionalRules = new ApplicableRuleset();
        conditionalRules.put(Tank.class, new ConditionalRule<>((x, y) -> x.getDurability() == 0, (x, y) -> {
            System.out.println("Conditional rules work");
            if (x.isDead()) {
                y.getBoard().putUnit(new EmptyUnit(x.getPosition()));
            } else {
                x.setDead(true);
                x.setActions(0);
                x.setGold(0);
                x.setDurability(3);
            }
        }));
        conditionalRules.put(Wall.class, new ConditionalRule<>((x, y) -> x.getDurability() == 0, (x, y) -> {
            y.getBoard().putUnit(new EmptyUnit(x.getPosition()));
            if (isOrthAdjToGoldMine(y, x.getPosition())) {
                y.getBoard().putFloor(new GoldMine(x.getPosition()));
            }
        }));

        for (Tank tank : s.getBoard().gatherUnits(Tank.class)) {
            conditionalRules.applyRules(s, tank);
        }

        System.out.println(t.toInfoString());
        t.setDead(false);
        t.setDurability(1);


        // Test IPlayerRule
        PlayerRuleset possiblePlayerActions = new PlayerRuleset();
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> x.getGold() >= 3, (x, y) -> {
                    x.setActions(x.getActions() + 1);
                    x.setGold(x.getGold()-3);
        }));
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> x.getGold() >= 5, (x, y) -> {
                    x.setActions(x.getActions() + 2);
                    x.setGold(x.getGold()-5);
        }));
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> x.getGold() >= 8, (x, y) -> {
                    x.setRange(x.getRange() + 1);
                    x.setGold(x.getGold()-8);
                }));
        possiblePlayerActions.put(Tank.class, Position.class, new PlayerActionRule<>((x, y, z) ->
            x.getActions() >= 1 && x.getPosition().distanceFrom(y) <= x.getRange()
                    && z.getBoard().getUnit(y).orElse(null) instanceof IDurable,
                (x, y, z) -> {
            x.setActions(x.getActions() - 1);
            if (z.getBoard().getUnit(y).orElse(null) instanceof IDurable unit) {
                if (unit instanceof Tank tank) {
                    boolean hit = false;
                    Random random = new Random(System.currentTimeMillis());
                    for (int i = x.getPosition().distanceFrom(y); i <= x.getRange(); ++i) {
                        if (random.nextBoolean()) {
                            hit = true;
                            break;
                        }
                    }
                    if (hit) {
                        tank.setDurability(tank.getDurability() - 1);
                        if (tank.getDurability() == 0) {
                            x.setGold(x.getGold() + tank.getGold());
                            tank.setGold(0);
                        }
                    }
                } else if (unit instanceof Wall wall) {
                    wall.setDurability(wall.getDurability() - 1);
                } else {
                    throw new Error("Unhandled tank shot onto " + unit.getClass().getName());
                }
            }
        }));

        System.out.println(possiblePlayerActions.getAllRulesForSubject(Tank.class));

        t.setGold(0);
        List<IPlayerRule<Tank, Tank>> possibleActions = possiblePlayerActions.applicableSelfRules(Tank.class, s, t);
        System.out.println("Applicable actions (should be 0): " + possibleActions.size());
        t.setGold(3);
        possibleActions = possiblePlayerActions.applicableSelfRules(Tank.class, s, t);
        System.out.println("Applicable actions (should be 1): " + possibleActions.size());
        System.out.println(t.toInfoString());
        for (IPlayerRule<Tank, Tank> action  : possibleActions) {
            action.apply(s, t, t);
        }
        System.out.println(t.toInfoString());

        lineOfSight(s, new Position(0,0), new Position(2,2));

        System.out.println(s.getBoard().toUnitString());
        System.out.println(s.getBoard().toFloorString());
    }
}