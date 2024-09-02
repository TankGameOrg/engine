package pro.trevor.tankgame.util;

import java.util.Optional;


public class Result<V, E> {
    private final V value;
    private final E error;

    protected Result(V value, E error) {
        this.value = value;
        this.error = error;
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }

    public V getValue() {
        if (error != null) {
            throw new Error(String.format("No value is present: error is '%s'", error));
        } else {
            return value;
        }
    }

    public E getError() {
        if (error == null) {
            throw new Error("No error is present");
        } else {
            return error;
        }
    }

    public Optional<E> asOptionalError() {
        return Optional.ofNullable(error);
    }

    public static <E> Result<Void, E> ok() {
        return new Result<>(null, null);
    }

    public static <V, E> Result<V, E> ok(V value) {
        return new Result<>(value, null);
    }

    public static <V, E> Result<V, E> error(E error) {
        assert error != null;
        return new Result<>(null, error);
    }
}
