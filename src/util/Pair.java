package util;

public record Pair<T, U>(T left, U right) {

    public static <T, U> Pair<T, U> of(T left, U right) {
        return new Pair<>(left, right);
    }

}
