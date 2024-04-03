package pro.trevor.tankgame.util;

public interface IVarQuadConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W... w);
}

