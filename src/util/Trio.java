package util;

public record Trio<T, U, V>(T left, U center, V right) {

    public static <T, U, V> Trio<T, U, V> of(T left, U center, V right) {
        return new Trio<>(left, center, right);
    }

}
