package state.board;

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
        System.arraycopy(diagAdj, 0, output, orthAdj.length, orthAdj.length + diagAdj.length);

        return output;
    }

}
