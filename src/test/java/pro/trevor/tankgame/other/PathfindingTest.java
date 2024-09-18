package pro.trevor.tankgame.other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PathfindingTest {

    private static final int BOARD_SIZE = 13;

    private static Board generateTestBoard(Position player, Position... walls) {
        Board board = new Board(BOARD_SIZE, BOARD_SIZE);
        board.putUnit(new Tank(new PlayerRef("Test"), player, Map.of()));
        for (Position wall : walls) {
            assert board.isValidPosition(wall);
            board.putUnit(new BasicWall(wall, 1));
        }
        return board;
    }

    private void testPossibleMovesIsExactly(Board board, Position start, int speed, Set<Position> moves) {
        Set<Position> generatedMoves = Util.allPossibleMoves(board, start, speed);
        Assertions.assertEquals(moves, generatedMoves);
    }

    /**
     * Finds all positions in a given radius about a given offset. Does not do bounds checking.
     * @param offset the middle of the radius.
     * @param radius the radius about the midpoint that positions will be generated for.
     * @return the set of all positions in the radius.
     */
    private Set<Position> positionsInRadius(Position offset, int radius) {
        Set<Position> positions = new HashSet<>();

        for (int i = offset.x() - radius; i <= offset.x() + radius; ++i) {
            for (int j = offset.y() - radius; j <= offset.y() + radius; ++j) {
                positions.add(new Position(i, j));
            }
        }

        return positions;
    }

    private void testVariableSpeedMoves(int speed) {
        testPossibleMovesIsExactly(generateTestBoard(new Position(speed, speed)), new Position(speed, speed), speed, positionsInRadius(new Position(speed, speed), speed));
    }

    @Test
    public void testUninterruptedPathfinding() {
        for (int speed = 0; speed < BOARD_SIZE / 2; ++speed) {
            testVariableSpeedMoves(speed);
        }
    }

    @Test
    public void testCannotMoveOntoWall() {
        Position start = new Position(1, 1);
        Board board = generateTestBoard(start, new Position(0, 1));
        Set<Position> expected = positionsInRadius(start, 1);
        expected.remove(new Position(0, 1));
        testPossibleMovesIsExactly(board, start, 1, expected);
    }

    @Test
    public void testCannotMoveThroughCorner() {
        Position start = new Position(1, 1);
        Board board = generateTestBoard(start, new Position(0, 1), new Position(1, 0));
        Set<Position> expected = positionsInRadius(start, 1);
        expected.remove(new Position(0, 0));
        expected.remove(new Position(0, 1));
        expected.remove(new Position(1, 0));
        testPossibleMovesIsExactly(board, start, 1, expected);
    }

    @Test
    public void testIsTrappedBetweenWalls() {
        Position start = new Position(1, 1);
        Board board = generateTestBoard(start, new Position(0, 1), new Position(1, 0), new Position(2, 1), new Position(1, 2));
        testPossibleMovesIsExactly(board, start, 1, Set.of(start));
    }

    @Test
    public void testIsTrappedBetweenWallsAtHighSpeeds() {
        Position start = new Position(1, 1);
        Board board = generateTestBoard(start, new Position(0, 1), new Position(1, 0), new Position(2, 1), new Position(1, 2));
        testPossibleMovesIsExactly(board, start, 5, Set.of(start));
    }

    /**
     * 1 E _
     * S 1 _
     * _ _ _
     * S = Start, E = End
     */
    @Test
    public void testLongRangePathfinding() {
        Position start = new Position(0, 1);
        Board board = generateTestBoard(start, new Position(0, 0), new Position(1, 1));
        Assertions.assertTrue(Util.allPossibleMoves(board, start, 3).contains(new Position(1, 0)));
    }

}
