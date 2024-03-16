package state.board;

import java.util.Objects;

public record Position (int x, int y) {

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
        return String.format("(%d, %d)", x, y);
    }
}
