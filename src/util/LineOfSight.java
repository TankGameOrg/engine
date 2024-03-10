package util;

import state.State;
import state.board.Position;
import state.board.unit.IWalkable;

import java.util.HashSet;
import java.util.Set;

public class LineOfSight {

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

    public static boolean hasLineOfSight(State s, Position p1, Position p2) {
        for (Position p : allPointsBetweenLineOfSight(p1, p2)) {
            if (!(s.getBoard().getUnit(p).orElse(null) instanceof IWalkable)) {
                return false;
            }
        }
        return true;
    }

}
