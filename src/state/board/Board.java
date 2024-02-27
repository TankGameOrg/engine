package state.board;

import state.board.floor.IFloor;
import state.board.floor.StandardFloor;
import state.board.unit.IUnit;
import state.board.unit.NullUnit;

import java.util.ArrayList;
import java.util.List;

public class Board {

    /*
     * Visual board representation
     *  +----X
     *  |
     *  |
     *  |
     *  |
     *  Y
     */
    private final IUnit[][] unitBoard;
    private final IFloor[][] floorBoard;

    public Board(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.unitBoard = new IUnit[width][height];
        this.floorBoard = new IFloor[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                unitBoard[i][j] = new NullUnit();
                floorBoard[i][j] = new StandardFloor(new Position(i, j));
            }
        }
    }

    public void putUnit(IUnit unit) {
        unitBoard[unit.getPosition().x()][unit.getPosition().y()] = unit;
    }

    public IUnit getUnit(Position p) {
        return unitBoard[p.x()][p.y()];
    }

    public void putFloor(IFloor floor) {
        floorBoard[floor.getPosition().x()][floor.getPosition().y()] = floor;
    }

    public IFloor getFloor(Position p) {
        return floorBoard[p.x()][p.y()];
    }


    public <T extends IUnit> List<T> gatherUnits(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < unitBoard.length; ++i) {
            for (int j = 0; j < unitBoard[0].length; ++j) {
                IUnit unit = unitBoard[i][j];
                try {
                    output.add(t.cast(unit));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }

    public <T extends IFloor> List<T> gatherFloors(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < floorBoard.length; ++i) {
            for (int j = 0; j < floorBoard[0].length; ++j) {
                IFloor floor = floorBoard[i][j];
                try {
                    output.add(t.cast(floor));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }


    private static <T extends IElement> String toGridString(T[][] board) {

        int pad = (int) Math.log10(board.length) + 1;

        StringBuilder sb = new StringBuilder();

        sb.repeat(' ', 2*pad);

        for (int i = 0; i < board.length; ++i) {
            sb.append((char)('A' + i)).append(' ');
        }

        sb.append("\n").repeat(' ', pad).append("+-");

        sb.repeat("--", board.length);

        sb.append('\n');

        for (int i = 0; i < board.length; ++i) {
            String paddedNumber = String.format(("%1$" + pad + "s"), (i + 1));
            sb.append(paddedNumber).append("| ");
            for (int j = 0; j < board[0].length; ++j) {
                sb.append(board[i][j].toString()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String toUnitString() {
        return toGridString(unitBoard);
    }

    public String toFloorString() {
        return toGridString(floorBoard);
    }

}
