package pro.trevor.tankgame.util;

public interface IVarQuadPredicate<T, U, V, W> {
    boolean test(T t, U u, V v, W... w);
}
