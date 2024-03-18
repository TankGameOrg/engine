package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.board.Position;

import java.util.Objects;

public record PointF(float x, float y) {

    public static PointF from(Position p) {
        return new PointF(p.x(), p.y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointF pointF = (PointF) o;
        return Float.compare(x, pointF.x) == 0 && Float.compare(y, pointF.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
