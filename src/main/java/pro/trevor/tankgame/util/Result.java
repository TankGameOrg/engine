package pro.trevor.tankgame.util;

public class Result<E> {
    private static final Result<?> OK = new Result<>(null);

    private final E error;

    protected Result(E error) {
        this.error = error;
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }

    public E getError() {
        if (error == null) {
            throw new Error("No error is present");
        } else {
            return error;
        }
    }

    public static <E> Result<E> ok() {
        return (Result<E>) OK;
    }

    public static <E> Result<E> error(E error) {
        return new Result<>(error);
    }
}
