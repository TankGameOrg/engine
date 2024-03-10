package util;

public record Triple<T, U, V>(T left, U center, V right) {

    public static <T, U, V> Triple<T, U, V> of(T left, U center, V right) {
        return new Triple<>(left, center, right);
    }

}
