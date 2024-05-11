package pro.trevor.tankgame.util;

import java.util.HashSet;
import java.util.Set;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;

public class LineOfSight {

    private static float ccw(PointF a, PointF b, PointF c) {
        return (b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x());
    }

    private static boolean pointsSeparatedCcw(PointF a, PointF b, PointF c, PointF d) {
        return (ccw(a, b, c) * ccw(a, b, d) < 0 && ccw(c, d, a) * ccw(c, d, b) < 0);
    }

    private static Set<Position> allSquaresBetweenLineOfSight(Position p1, Position p2) {
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
            for (int x = minx; x <= maxx; ++x) {
                for (int y = miny; y <= maxy; ++y) {
                    // X represents the given position of the square
                    // each position represents a square given by XABC as shown below
                    //
                    // X---C
                    // |   |
                    // A---B
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
                        y1 = y + 1;
                        // C
                        x2 = x + 1;
                        y2 = y;
                    } else {
                        // X
                        x1 = x;
                        y1 = y;
                        // B
                        x2 = x + 1;
                        y2 = y + 1;
                    }
                    if (pointsSeparatedCcw(
                            new PointF(p1.x() + 0.5f, p1.y() + 0.5f),
                            new PointF(p2.x() + 0.5f, p2.y() + 0.5f),
                            new PointF(x1, y1),
                            new PointF(x2, y2))) {
                        points.add(new Position(x, y));
                    }
                }
            }
        }

        points.remove(p1);
        points.remove(p2);

        return points;
    }

    private static Set<Position> allCartesianAlignedPointsBetween(Position p1, Position p2) {
        // Double our resolution; line of sight is center to center
        Position p1_ = new Position(p1.x() * 2 + 1, p1.y() * 2 + 1);
        Position p2_ = new Position(p2.x() * 2 + 1, p2.y() * 2 + 1);

        // The slope is the same despite our resolution
        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();

        if (dx == 0 || dy == 0) {
            return new HashSet<>(0);
        }

        int minx = Math.min(p1_.x(), p2_.x());
        int maxx = Math.max(p1_.x(), p2_.x());

        Set<Position> output = new HashSet<>((maxx - minx) / 2);

        // y - p1.y() = (dy/dx) * (x - p1.x())
        // y * dx = p1.y() * dx + dy * (x - p1.x())
        // Use even-numbered X coordinates (corresponding to X/2 on the standard resolution board)
        for (int i = minx + 1; i <= maxx; i += 2) {
            // Calculate y * dx instead of needing to use a floating point number
            int ydx = dx * p1_.y() + dy * (i - p1_.x());
            int y = ydx / dx;
            // Ensure the
            if (y * dx == ydx && y % 2 == 0) {
                // y is an integer, this point to the list; divide by two first to go back to
                // standard resolution
                output.add(new Position(i / 2, y / 2));
            }
        }
        return output;
    }

    public static boolean hasLineOfSightV3(State s, Position p1, Position p2) {
        if (!s.getBoard().isValidPosition(p1) || !s.getBoard().isValidPosition(p2)) {
            return false;
        }
        for (Position p : allSquaresBetweenLineOfSight(p1, p2)) {
            if (!s.getBoard().isAbleToShootThrough(p)) {
                return false;
            }
        }
        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();

        // an integer multiplied by the sign of the slope (can be zero)
        int sign = dy * dx;

        Set<Position> corners = allCartesianAlignedPointsBetween(p1, p2);

        Position q1, q2;
        for (Position corner : corners) {
            int x = corner.x();
            int y = corner.y();
            if (sign >= 0) {
                q1 = new Position(x - 1, y);
                q2 = new Position(x, y - 1);
            } else {
                q1 = new Position(x - 1, y - 1);
                q2 = new Position(x, y);
            }
            if (!s.getBoard().isAbleToShootThrough(q1) || !s.getBoard().isAbleToShootThrough(q2)) {
                return false;
            }
        }

        // has line of sight if was not blocked by corners or points
        return true;
    }

    public static boolean hasLineOfSightV4(State s, Position p1, Position p2) {
        for (Position p : allSquaresBetweenLineOfSight(p1, p2)) {
            if (!s.getBoard().isAbleToShootThrough(p)) {
                return false;
            }
        }

        int dx = p2.x() - p1.x();
        int dy = p2.y() - p1.y();

        // an integer multiplied by the sign of the slope (can be zero)
        int sign = dy * dx;

        Set<Position> corners = allCartesianAlignedPointsBetween(p1, p2);

        Position q1, q2;
        boolean hitLeft = false;
        boolean hitRight = false;
        for (Position corner : corners) {
            int x = corner.x();
            int y = corner.y();
            if (sign >= 0) {
                q1 = new Position(x - 1, y);
                q2 = new Position(x, y - 1);
            } else {
                q1 = new Position(x - 1, y - 1);
                q2 = new Position(x, y);
            }
            if (!s.getBoard().isAbleToShootThrough(q1)) {
                hitLeft = true;
            }
            if (!s.getBoard().isAbleToShootThrough(q2)) {
                hitRight = true;
            }

            // does not have line of sight if passed through both a left corner and a right corner
            if (hitLeft && hitRight) {
                return false;
            }
        }

        // has line of sight if was not blocked by corners or points
        return true;
    }
}
