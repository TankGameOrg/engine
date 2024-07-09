package pro.trevor.tankgame.util.function;

public interface IVarTriFunction<T, U, V, R> {
    R accept(T t, U u, V... v);
}