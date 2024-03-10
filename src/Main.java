import rule.definition.*;
import rule.definition.enforcer.EnforcerRuleset;
import rule.definition.enforcer.MaximumEnforcer;
import rule.definition.enforcer.MinimumEnforcer;
import rule.definition.player.IPlayerRule;
import rule.definition.player.PlayerActionRule;
import rule.definition.player.PlayerRuleset;
import rule.definition.player.PlayerSelfActionRule;
import state.board.Position;
import state.State;
import state.board.Util;
import state.board.floor.GoldMine;
import state.board.unit.*;
import state.meta.Council;
import state.meta.Player;

import java.util.*;

import static rule.annotation.AnnotationProcessor.getRuleset;

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

    private static boolean pointsSeparated(int a, int b, int c, int x1, int y1, int x2, int y2) {
        int fx1 = a * x1 + b * y1 + c;
        int fx2 = a * x2 + b * y2 + c;
        int corner = a * x1 + b * y2 + c;

        // points are separated by the line if the signs are opposite; detect if a corner is hit
        return (fx1 * fx2) <= 0 || corner == 0;
    }

    private static Set<Position> allPointsBetweenLineOfSight(Position p1, Position p2) {
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
                    // X represents the given position of the square
                    // each position represents a square given by XABC as shown below
                    // A---B
                    // |   |
                    // X---C
                    // x1y1|x2y2 are thus eiter A|C or X|B
                    // X is +0,+0
                    // A is +0,+1
                    // B is +1,+1
                    // C is +1,+0
                    // we need to check if the line divides two corners of the square
                    // use A|C if slope's sign is positive or zero
                    // use X|B if the slope's sign is negative
                    int x1, y1, x2, y2;

                    // dy/dx is the slope but dy*dx has the same sign but is safer and faster
                    int sign = dy * dx;
                    if (sign >= 0) {
                        // A
                        x1 = x;
                        y1 = y+1;
                        // C
                        x2 = x+1;
                        y2 = y;
                    } else {
                        // X
                        x1 = x;
                        y1 = y;
                        // B
                        x2 = x+1;
                        y2 = y+1;
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
        for (Position p : allPointsBetweenLineOfSight(p1, p2)) {
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
        unitInvariants.put(Council.class, new MinimumEnforcer<>(Council::getCoffer, Council::setCoffer, 0));


        State s = new State(11, 11, new HashSet<>());
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
        assert t.getActions() == 0;


        // Test TickActionRule
        ApplicableRuleset tickRules = new ApplicableRuleset();
        tickRules.put(Tank.class, new TickActionRule<>((x, y) -> {
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
        assert t.getGold() == 3;

        // Test IConditionalRule
        ApplicableRuleset conditionalRules = new ApplicableRuleset();

        // Handle tank destruction
        conditionalRules.put(Tank.class, new ConditionalRule<>((x, y) -> x.getDurability() == 0, (x, y) -> {
            if (x.isDead()) {
                y.getBoard().putUnit(new EmptyUnit(x.getPosition()));
                Player tankPlayer = x.getPlayer();
                y.getCouncil().getCouncillors().remove(tankPlayer);
                y.getCouncil().getSenators().add(tankPlayer);
            } else {
                x.setDead(true);
                x.setActions(0);
                x.setGold(0);
                x.setDurability(3);
                y.getCouncil().getCouncillors().add(x.getPlayer());
            }
        }));

        // Handle wall destruction
        conditionalRules.put(Wall.class, new ConditionalRule<>((x, y) -> x.getDurability() == 0, (x, y) -> {
            y.getBoard().putUnit(new EmptyUnit(x.getPosition()));
            if (isOrthAdjToGoldMine(y, x.getPosition())) {
                y.getBoard().putFloor(new GoldMine(x.getPosition()));
            }
        }));

        for (Tank tank : s.getBoard().gatherUnits(Tank.class)) {
            conditionalRules.applyRules(s, tank);
        }

        System.out.println(s.getCouncil().getCouncillors());
        assert s.getCouncil().getCouncillors().contains(t.getPlayer());
        assert !s.getCouncil().getSenators().contains(t.getPlayer());

        System.out.println(t.toInfoString());
        t.setDead(false);
        t.setDurability(1);


        // Test IPlayerRule with v3 player actions
        PlayerRuleset possiblePlayerActions = new PlayerRuleset();

        // Buy 1 action
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> !x.isDead() && x.getGold() >= 3, (x, y) -> {
                    x.setActions(x.getActions() + 1);
                    x.setGold(x.getGold()-3);
        }));

        // Buy 2 actions
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> !x.isDead() && x.getGold() >= 5, (x, y) -> {
                    x.setActions(x.getActions() + 2);
                    x.setGold(x.getGold()-5);
        }));

        // Upgrade range
        possiblePlayerActions.putSelfRule(Tank.class,
                new PlayerSelfActionRule<>((x, y) -> !x.isDead() && x.getGold() >= 8, (x, y) -> {
                    x.setRange(x.getRange() + 1);
                    x.setGold(x.getGold()-8);
                }));

        // Shoot
        possiblePlayerActions.put(Tank.class, Position.class, new PlayerActionRule<>((x, y, z) ->
            !x.isDead() && x.getActions() >= 1 && x.getPosition().distanceFrom(y) <= x.getRange()
                    && hasLineOfSight(s, x.getPosition(), y)  && z.getBoard().getUnit(y).orElse(null) instanceof IDurable,
                (x, y, z) -> {
            if (z.getBoard().getUnit(y).orElse(null) instanceof IDurable unit) {
                x.setActions(x.getActions() - 1);
                if (unit instanceof Tank tank) {
                    if (tank.isDead()) {
                        tank.setDurability(tank.getDurability() - 1);
                    } else {
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

        // corner case, yes interrupt
        s.getBoard().putUnit(new Tank(new Position(5, 1), 0, 0, 3, 2));
        System.out.printf("line of sight (expect false): %b\n", hasLineOfSight(s, zero, new Position(5,2)));

        System.out.println(s.getBoard().toUnitString());
        System.out.println(s.getBoard().toFloorString());

        getRuleset(3);
    }
}