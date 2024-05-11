package pro.trevor.tankgame.util;

import java.util.Objects;

public record Triple<T, U, V>(T left, U center, V right) {

    public static <T, U, V> Triple<T, U, V> of(T left, U center, V right) {
        return new Triple<>(left, center, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(left, triple.left)
                && Objects.equals(center, triple.center)
                && Objects.equals(right, triple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, center, right);
    }
}
