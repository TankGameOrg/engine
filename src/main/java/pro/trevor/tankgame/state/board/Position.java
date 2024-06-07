package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.util.IJsonObject;

import java.util.Objects;

public record Position (int x, int y) implements IJsonObject {

    public Position(String boardString) {
        this(boardString.charAt(0) - 'A', Integer.parseInt(boardString.substring(1)) - 1);
    }

    public Position(JSONObject json) {
        this(json.getInt("x"), json.getInt("y"));
    }

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

    public String toBoardString() {
        return String.format("%c%d", x + ('A'), y + 1);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("class", this.getClass().getName());
        json.put("x", x);
        json.put("y", y);
        return json;
    }
}
