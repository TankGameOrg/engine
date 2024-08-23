package pro.trevor.tankgame.state.board;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.state.board.floor.UnwalkableFloor;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.IGatherable;
import pro.trevor.tankgame.util.JsonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonType(name = "Board")
public class Board implements IMetaElement, IGatherable {

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

    public Board(JSONObject json) {
        JSONArray unitBoard = json.getJSONArray("unit_board");
        JSONArray floorBoard = json.getJSONArray("floor_board");

        assert unitBoard.length() == floorBoard.length();
        assert unitBoard.getJSONArray(0).length() == floorBoard.getJSONArray(0).length();

        this.height = unitBoard.length();
        this.width = unitBoard.getJSONArray(0).length();

        this.unitBoard = new IUnit[height][width];
        this.floorBoard = new IFloor[height][width];

        for (int y = 0; y < height; ++y) {
            JSONArray unitBoardRow = unitBoard.getJSONArray(y);
            JSONArray floorBoardRow = floorBoard.getJSONArray(y);
            for (int x = 0; x < width; ++x) {
                JSONObject unitJson = unitBoardRow.getJSONObject(x);
                JSONObject floorJson = floorBoardRow.getJSONObject(x);

                Object decodedUnit = Codec.decodeJson(unitJson);
                Object decodedFloor = Codec.decodeJson(floorJson);

                if (decodedUnit instanceof IUnit unit) {
                    putUnit(unit);
                } else {
                    throw new Error("JSON contains a class that is not IUnit: " + decodedUnit.getClass().getName());
                }

                if (decodedFloor instanceof IFloor floor) {
                    putFloor(floor);
                } else {
                    throw new Error("JSON contains a class that is not IFloor: " + decodedFloor.getClass().getName());
                }
            }
        }
    }

    public boolean isValidPosition(Position p) {
        return (p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height);
    }

    private <T extends IElement> boolean putElementOnBoard(T[][] board, T element) {
        if (isValidPosition(element.getPosition())) {
            board[element.getPosition().y()][element.getPosition().x()] = element;
            return true;
        }
        return false;
    }

    private <T extends IElement> Optional<T> getElementOnBoard(T[][] board, Position p) {
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

    public Optional<IPlayerElement> getPlayerElement(PlayerRef playerRef) {
        return gatherUnits(IPlayerElement.class).stream().filter((p) -> p.getPlayerRef().equals(playerRef) ).findAny();
    }

    // Returns the unit at the position if there is one.
    // If there is no unit at the position, then the floor is returned.
    public Optional<IElement> getUnitOrFloor(Position p) {
        IElement unit = getUnit(p).orElse(null);
        if (unit != null && !(unit instanceof EmptyUnit))
            return Optional.of(unit);
        IElement floor = getFloor(p).orElse(null);
        return Optional.ofNullable(floor);
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

    @Override
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

    @Override
    public List<Object> gatherAll() {
        return Stream.concat(gatherAllElements().stream(), gather(Position.class).stream()).collect(Collectors.toList());
    }

    public List<IElement> gatherAllElements() {
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
    public String toString() {
        return '\n' + toUnitString() + '\n' + toFloorString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
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

    /**
     * Return all valid positions on the board
     * @return
     */
    public List<Position> getAllPositions() {
        List<Position> output = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                output.add(new Position(x, y));
            }
        }
        return output;
    }
}
