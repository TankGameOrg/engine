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

    private static boolean pointsSeparated(int a, int b, int c, float x1, float y1, float x2, float y2) {
        float fx1 = a * x1 + b * y1 - c;
        float fx2 = a * x2 + b * y2 - c;

        // points are separated by the line if the signs are opposite, or points hit a corner if fx1 == fx2
        return (fx1 * fx2) < 0 || fx1 == fx2;
    }

    private static Set<Position> pointsBetweenLineOfSight(State s, Position p1, Position p2) {
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
                    // A---B
                    // | X |
                    // D---C
                    // A is -.5,-.5
                    // B is +.5,-.5
                    // C is +.5,+.5
                    // D is -.5,+.5
                    // use A|C if slope is positive or zero
                    // use B|D if the slope is negative
                    float x1, y1, x2, y2;
                    if (slope >= 0) {
                        // A
                        x1 = x-0.5f;
                        y1 = y-0.5f;
                        // C
                        x2 = x+0.5f;
                        y2 = y+0.5f;
                    } else {
                        // B
                        x1 = x+0.5f;
                        y1 = y-0.5f;
                        // D
                        x2 = x-0.5f;
                        y2 = y+0.5f;
                    }
                    if (pointsSeparated(a, b, c, x1, y1, x2, y2)) {
                        points.add(new Position(x, y));
                    }
                }
            }
        }

        points.remove(p1);
        points.remove(p2);

        return points;
    }

    private static boolean hasLineOfSight(State s, Position p1, Position p2) {
        for (Position p : pointsBetweenLineOfSight(s, p1, p2)) {
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


        Position zero = new Position(0, 0);
        // horizontal, no interrupt
        System.out.printf("line of sight (expect true): %b\n", hasLineOfSight(s, zero, new Position(5,0)));
        // vertical, yes interrupt
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(0,5)));
        // diagonal, check corners, yes interrupt
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(1,1)));
        // adjacent horizontal, implicit no interrupt
        System.out.printf("line of sight (expect true): %b\n", hasLineOfSight(s, zero, new Position(0,1)));

        System.out.println(s.getBoard().toUnitString());
        System.out.println(s.getBoard().toFloorString());
    }
}