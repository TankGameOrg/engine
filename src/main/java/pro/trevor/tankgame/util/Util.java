package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;

import java.util.*;
import java.util.function.Function;

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

    public static Set<Position> allAdjacentMovablePositions(Board board, Position p) {
        Set<Position> output = new HashSet<>();

        Position left = new Position(p.x() - 1, p.y());
        Position right = new Position(p.x() + 1, p.y());
        Position up = new Position(p.x(), p.y() - 1);
        Position down = new Position(p.x(), p.y() + 1);
        Position upLeft = new Position(p.x() - 1, p.y() - 1);
        Position upRight = new Position(p.x() + 1, p.y() - 1);
        Position downRight = new Position(p.x() + 1, p.y() + 1);
        Position downLeft = new Position(p.x() - 1, p.y() + 1);

        if (board.isWalkable(left)) {
            output.add(left);
            if (board.isWalkable(upLeft)) {
                output.add(upLeft);
            }
            if (board.isWalkable(downLeft)) {
                output.add(downLeft);
            }
        }

        if (board.isWalkable(right)) {
            output.add(right);
            if (board.isWalkable(upRight)) {
                output.add(upRight);
            }
            if (board.isWalkable(downRight)) {
                output.add(downRight);
            }
        }

        if (board.isWalkable(up)) {
            output.add(up);
            if (board.isWalkable(upLeft)) {
                output.add(upLeft);
            }
            if (board.isWalkable(upRight)) {
                output.add(upRight);
            }
        }

        if (board.isWalkable(down)) {
            output.add(down);
            if (board.isWalkable(downLeft)) {
                output.add(downLeft);
            }
            if (board.isWalkable(downRight)) {
                output.add(downRight);
            }
        }

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

    public static Set<Position> getSpacesInRange(Board board, Position p, int range){
        Set<Position> output = new HashSet<>();
        for (int i = 0; i <= range; ++i) {
            for (int j = 0; j <= range; ++j) {
                Position p1 = new Position(p.x() + i, p.y() + j);
                if(board.isValidPosition(p1)) output.add(p1);
                Position p2 = new Position(p.x() - i, p.y() - j);
                if(board.isValidPosition(p2)) output.add(p2);
                Position p3 = new Position(p.x() - i, p.y() + j);
                if(board.isValidPosition(p3)) output.add(p3);
                Position p4 = new Position(p.x() + i, p.y() - j);
                if(board.isValidPosition(p4)) output.add(p4);
            }
        }
        return output;
    }

    public static Set<Position> possibleMoves(State state, Position p, int speed) {
        HashMap<Position, Integer> output = new HashMap<>();
        possibleMovesHelper(output, state, p, speed);
        return output.keySet();
    }

    private static void possibleMovesHelper(HashMap<Position, Integer> visited, State state, Position p, int speed) {
        if (visited.getOrDefault(p, -1) >= speed || (!state.getBoard().isWalkable(p) && !visited.isEmpty())) {
            return;
        }

        visited.put(p, speed);

        if (speed == 0) {
            return;
        }

        for (Position position : allAdjacentMovablePositions(state.getBoard(), p)) {
            possibleMovesHelper(visited, state, position, speed - 1);
        }
    }

    public static boolean canMoveTo(State state, Position s, Position e, int speed) {
        Set<Position> possibleMoves = possibleMoves(state, s, speed);
        return possibleMoves.contains(e);
    }

    public static String toString(Collection<?> items) {
        return toString(items, 0, Object::toString);
    }

    public static <T> String toString(Collection<T> items, Function<T, String> toStringFunction) {
        return toString(items, 0, toStringFunction);
    }

    public static String toString(Collection<?> items, int indent) {
        return toString(items, indent, Object::toString);
    }

    public static <T> String toString(Collection<T> items, int indent, Function<T, String> toStringFunction) {
        StringBuilder sb = new StringBuilder("[\n");
        items.forEach((x) -> sb.repeat(' ', indent).append(toStringFunction.apply(x)).append(",\n"));
        if (!items.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.append("]\n").toString();
    }

    public static <T> T toType(Object o, Class<T> type) {
        if (type.isAssignableFrom(o.getClass())) {
            return type.cast(o);
        } else {
            throw new Error(String.format("Could not convert %s to %s", o.getClass().getSimpleName(), type.getSimpleName()));
        }
    }

    public static <T> T toType(Object o) {
        return (T) o;
    }

}
