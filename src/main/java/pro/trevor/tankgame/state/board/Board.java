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
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
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
                unitBoard[y][x] = null;
                floorBoard[y][x] = null;
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

    public boolean putUnit(IUnit unit) {
        return putElementOnBoard(unitBoard, unit);
    }

    public Optional<IUnit> getUnit(Position p) {
        if (!isValidPosition(p)) {
            return Optional.empty();
        }
        IUnit unit = unitBoard[p.y()][p.x()];
        return Optional.of(Objects.requireNonNullElseGet(unit, () -> new EmptyUnit(p)));
    }

    public boolean putFloor(IFloor floor) {
        return putElementOnBoard(floorBoard, floor);
    }

    public Optional<IFloor> getFloor(Position p) {
        if (!isValidPosition(p)) {
            return Optional.empty();
        }
        IFloor floor = floorBoard[p.y()][p.x()];
        return Optional.of(Objects.requireNonNullElseGet(floor, () -> new WalkableFloor(p)));
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

    private static <T extends IElement> String toGridString(Board board, BiFunction<Board, Position, Character> getChar) {

        int pad = (int) Math.log10(board.width) + 1;

        StringBuilder sb = new StringBuilder();

        sb.repeat(' ', 2 * pad);

        for (int i = 0; i < board.width; ++i) {
            sb.append((char) ('A' + i)).append(' ');
        }

        sb.append("\n").repeat(' ', pad).append("+-");

        sb.repeat("--", board.width);

        sb.append('\n');

        for (int i = 0; i < board.width; ++i) {
            String paddedNumber = String.format(("%1$" + pad + "s"), (i + 1));
            sb.append(paddedNumber).append("| ");
            for (int j = 0; j < board.height; ++j) {
                sb.append(getChar.apply(board, new Position(j, i))).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String toUnitString() {
        return toGridString(this, (b, p) -> b.getUnit(p).get().toBoardCharacter());
    }

    public String toFloorString() {
        return toGridString(this, (b, p) -> b.getFloor(p).get().toBoardCharacter());
    }

    @Override
    public String toString() {
        return '\n' + toUnitString() + '\n' + toFloorString();
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("width", width);
        output.put("height", height);
        output.put("units", gather(IUnit.class).stream().map(IJsonObject::toJson).toList());
        output.put("floors", gather(IFloor.class).stream().map(IJsonObject::toJson).toList());
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
