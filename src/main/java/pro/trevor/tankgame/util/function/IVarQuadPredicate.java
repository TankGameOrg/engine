package pro.trevor.tankgame.util.function;

public interface IVarQuadPredicate<T, U, V, W> {
    boolean test(T t, U u, V v, W... w);
}
