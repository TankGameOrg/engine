package state.board;

import java.util.ArrayList;
import java.util.List;

public class Board {

    /*
     *  Y
     *  |
     *  |
     *  |
     *  |
     *  +----X
     */

    private final IUnit[][] board;

    public Board(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.board = new IUnit[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                board[i][j] = new NullUnit();
            }
        }
    }

    public void putUnit(IUnit unit) {
        board[unit.getPosition().x()][unit.getPosition().y()] = unit;
    }

    public IUnit getUnit(Position p) {
        return board[p.x()][p.y()];
    }

    public <T extends IUnit> List<T> gather(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                IUnit unit = board[i][j];
                try {
                    output.add(t.cast(unit));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }

}
