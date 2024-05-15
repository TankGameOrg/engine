package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Util {

    public static Position[] orthogonallyAdjacentPositions(Position p) {
        Position[] output = new Position[4];
        output[0] = new Position(p.x() - 1, p.y());
        output[1] = new Position(p.x() + 1, p.y());
        output[2] = new Position(p.x(), p.y() - 1);
        output[3] = new Position(p.x(), p.y() + 1);
        return output;
    }

    public static Position[] diagonallyAdjacentPositions(Position p) {
        Position[] output = new Position[4];
        output[0] = new Position(p.x() - 1, p.y() - 1);
        output[1] = new Position(p.x() + 1, p.y() + 1);
        output[2] = new Position(p.x() + 1, p.y() - 1);
        output[3] = new Position(p.x() - 1, p.y() + 1);
        return output;
    }

    public static Position[] allAdjacentPositions(Position p) {
        Position[] orthAdj = orthogonallyAdjacentPositions(p);
        Position[] diagAdj = diagonallyAdjacentPositions(p);

        Position[] output = new Position[orthAdj.length + diagAdj.length];
        System.arraycopy(orthAdj, 0, output, 0, orthAdj.length);
        System.arraycopy(diagAdj, 0, output, orthAdj.length, diagAdj.length);

        return output;
    }

    public static boolean isOrthAdjToMine(State state, Position p) {
        for (Position x : Util.orthogonallyAdjacentPositions(p)) {
            if (state.getBoard().getFloor(x).orElse(null) instanceof GoldMine) {
                return true;
            }
        }
        return false;
    }

    public static void findAllConnectedMines(Set<Position> positions, State state, Position p) {
        if (!positions.contains(p)) {
            positions.add(p);
            Arrays.stream(Util.orthogonallyAdjacentPositions(p))
                    .filter((x) -> state.getBoard().getFloor(x).orElse(null) instanceof GoldMine)
                    .forEach((x) -> findAllConnectedMines(positions, state, x));
        }
    }

    public static Set<Position> getSpacesInRange(Position p, int range){
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

    public static boolean canMoveTo(State state, Position s, Position e) {
        Position[] adjacent = allAdjacentPositions(s);
        if (!Arrays.stream(adjacent).toList().contains(e)) {
            return false;
        } else if (!state.getBoard().isWalkable(e)) {
            return false;
        }

        int dx = e.x() - s.x();
        int dy = e.y() - s.y();

        if (dx == 0 || dy == 0) {
            return true;
        } else {
            Position adjY = new Position(s.x() + dx, s.y());
            Position adjX = new Position(s.x(), s.y() + dy);
            return state.getBoard().isWalkable(adjY) || state.getBoard().isWalkable(adjX);
        }
    }

    public static String toString(Collection<?> items) {
        return toString(items, 2);
    }

    public static String toString(Collection<?> items, int indent) {
        StringBuilder sb = new StringBuilder("[\n");
        items.forEach((x) -> sb.repeat(' ', indent).append(x).append(",\n"));
        return sb.append("]\n").toString();
    }

    public static <T> T toType(Object o, Class<T> type) {
        if (type.isAssignableFrom(o.getClass())) {
            return type.cast(o);
        } else {
            throw new Error(String.format("Could not convert %s to %s", o.getClass().getSimpleName(), type.getSimpleName()));
        }
    }

}
