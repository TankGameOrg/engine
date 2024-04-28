package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;

import java.util.Objects;

public record Position (int x, int y) implements IJsonObject {

    public int distanceFrom(Position p) {
        return Math.max(Math.abs(x - p.x), Math.abs(y - p.y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return toBoardString();
    }

    public String toLongString() {
        return String.format("(%d, %d)", x, y);
    }
    public String toBoardString() {
        return String.format("%c%d", x+('A'), y+1);
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();
        output.put("type", "position");
        output.put("x", x);
        output.put("y", y);
        return output;
    }

    @Override
    public JSONObject toShortJson() {
        JSONObject output = new JSONObject();
        output.put("position", toBoardString());
        return output;
    }

    public static Position fromJson(JSONObject json) {
        assert json.get("type").equals("position");
        return new Position(json.getInt("x"), json.getInt("y"));
    }
}
