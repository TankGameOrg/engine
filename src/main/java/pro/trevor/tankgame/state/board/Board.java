package pro.trevor.tankgame.state.board;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Codec;
import pro.trevor.tankgame.attribute.Entity;
import pro.trevor.tankgame.state.board.floor.ImpassibleFloor;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;
import pro.trevor.tankgame.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@JsonType(name = "Board")
public class Board implements IJsonObject, Entity {

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
        this(json.getInt("width"), json.getInt("height"));

        JSONArray units = json.getJSONArray("units");
        JSONArray floors = json.getJSONArray("floors");

        for(int i = 0; i < units.length(); ++i) {
            Object decodedUnit = Codec.decodeJson(units.getJSONObject(i));

            if (decodedUnit instanceof IUnit unit) {
                if(!putUnit(unit)) {
                    throw new Error("The unit at " + unit.getPosition() + " is not in valid position. Unit: " + decodedUnit);
                }
            } else {
                throw new Error("JSON contains a class that is not IUnit: " + decodedUnit.getClass().getName());
            }
        }

        for(int i = 0; i < floors.length(); ++i) {
            Object decodedFloor = Codec.decodeJson(floors.getJSONObject(i));

            if (decodedFloor instanceof IFloor floor) {
                if(!putFloor(floor)) {
                    throw new Error("The floor at " + floor.getPosition() + " is not in valid position. Floor: " + decodedFloor);
                }
            } else {
                throw new Error("JSON contains a class that is not IFloor: " + decodedFloor.getClass().getName());
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

    // Returns the unit at the position if there is one.
    // If there is no unit at the position, then the floor is returned.
    public Optional<IElement> getUnitOrFloor(Position p) {
        IElement unit = getUnit(p).orElse(null);
        if (unit != null && !(unit instanceof EmptyUnit))
            return Optional.of(unit);
        IElement floor = getFloor(p).orElse(null);
        return Optional.ofNullable(floor);
    }

    public <T> Stream<T> gatherUnits(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                IUnit unit = unitBoard[y][x];
                if (t.isInstance(unit)) {
                    output.add(t.cast(unit));
                }
            }
        }
        return output.stream();
    }

    public <T> Stream<T> gatherFloors(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                IFloor floor = floorBoard[y][x];
                if (t.isInstance(floor)) {
                    output.add(t.cast(floor));
                }
            }
        }
        return output.stream();
    }

    public Stream<Object> gather() {
        return Stream.concat(gatherUnits(IElement.class), gatherFloors(IElement.class));
    }

    public boolean isWalkable(Position p) {
        return (getUnit(p).orElse(null) instanceof EmptyUnit)
                && (getFloor(p).orElse(new ImpassibleFloor(p)).isWalkable(this));
    }

    /**
     * Check if a location is on the board and not occupied by a unit or floor
     */
    public boolean isEmpty(Position position) {
        return getUnitOrFloor(position)
            .map((element) -> element.getClass().equals(WalkableFloor.class))
            .orElse(false); // Position is not on the board
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

    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("width", width);
        output.put("height", height);
        output.put("units", gather(IUnit.class)
            .filter(unit -> !unit.getClass().equals(EmptyUnit.class))
            .map(IJsonObject::toJson).toList());
        output.put("floors", gather(IFloor.class)
            .filter(floor -> !floor.getClass().equals(WalkableFloor.class))
            .map(IJsonObject::toJson).toList());
        return output;
    }

    /**
     * Return all valid positions on the board
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
