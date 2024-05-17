package pro.trevor.tankgame.state.board;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.board.floor.UnwalkableFloor;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board implements IMetaElement {

    private final IUnit[][] unitBoard;
    private final IFloor[][] floorBoard;

    private final int width;
    private final int height;

    public Board(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.width = width;
        this.height = height;
        this.unitBoard = new IUnit[height][width];
        this.floorBoard = new IFloor[height][width];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                unitBoard[y][x] = new EmptyUnit(new Position(x, y));
                floorBoard[y][x] = new WalkableFloor(new Position(x, y));
            }
        }
    }

    public boolean isValidPosition(Position p) {
        return (p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height);
    }

    private <T extends IPositioned> boolean putElementOnBoard(T[][] board, T element) {
        if (isValidPosition(element.getPosition())) {
            board[element.getPosition().y()][element.getPosition().x()] = element;
            return true;
        }
        return false;
    }

    private <T extends IPositioned> Optional<T> getElementOnBoard(T[][] board, Position p) {
        if (isValidPosition(p)) {
            return Optional.of(board[p.y()][p.x()]);
        }
        return Optional.empty();
    }

    public boolean putUnit(IUnit unit) {
        return putElementOnBoard(unitBoard, unit);
    }

    public Optional<IUnit> getUnit(Position p) {
        return getElementOnBoard(unitBoard, p);
    }

    public boolean putFloor(IFloor floor) {
        return putElementOnBoard(floorBoard, floor);
    }

    public Optional<IFloor> getFloor(Position p) {
        return getElementOnBoard(floorBoard, p);
    }

    public <T> List<T> gatherUnits(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                IUnit unit = unitBoard[y][x];
                if (t.isInstance(unit)) {
                    output.add(t.cast(unit));
                }
            }
        }
        return output;
    }

    public <T> List<T> gatherFloors(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                IFloor floor = floorBoard[y][x];
                if (t.isInstance(floor)) {
                    output.add(t.cast(floor));
                }
            }
        }
        return output;
    }

    public <T> List<T> gather(Class<T> t) {
        if (Position.class.isAssignableFrom(t)) {
            List<T> positions = new ArrayList<>();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    positions.add((T) new Position(x, y));
                }
            }
            return positions;
        }

        if (IUnit.class.isAssignableFrom(t)) {
            return gatherUnits(t);
        } else if (IFloor.class.isAssignableFrom(t)) {
            return gatherFloors(t);
        } else {
            throw new Error("Unexpected class: " + t.getSimpleName());
        }
    }

    public List<IElement> gatherAll() {
        return Stream.concat(gather(IUnit.class).stream(), gather(IFloor.class).stream()).collect(Collectors.toList());
    }

    public boolean isWalkable(Position p) {
        return (getUnit(p).orElse(null) instanceof EmptyUnit)
                && (getFloor(p).orElse(new UnwalkableFloor(p)).isWalkable(this));
    }

    public boolean isAbleToShootThrough(Position p) {
        return getUnit(p).orElse(null) instanceof EmptyUnit;
    }

    private static <T extends IElement> String toGridString(T[][] board) {

        int pad = (int) Math.log10(board.length) + 1;

        StringBuilder sb = new StringBuilder();

        sb.repeat(' ', 2 * pad);

        for (int i = 0; i < board.length; ++i) {
            sb.append((char) ('A' + i)).append(' ');
        }

        sb.append("\n").repeat(' ', pad).append("+-");

        sb.repeat("--", board.length);

        sb.append('\n');

        for (int i = 0; i < board.length; ++i) {
            String paddedNumber = String.format(("%1$" + pad + "s"), (i + 1));
            sb.append(paddedNumber).append("| ");
            for (int j = 0; j < board[0].length; ++j) {
                sb.append(board[i][j].toBoardCharacter()).append(' ');
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

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "board");
        JSONArray units = new JSONArray();
        JSONArray floors = new JSONArray();
        for (int y = 0; y < height; ++y) {
            JSONArray unit = new JSONArray();
            JSONArray floor = new JSONArray();
            for (int x = 0; x < width; ++x) {
                unit.put(unitBoard[y][x].toJson());
                floor.put(floorBoard[y][x].toJson());
            }
            units.put(unit);
            floors.put(floor);
        }
        output.put("unit_board", units);
        output.put("floor_board", floors);
        return output;
    }
}
