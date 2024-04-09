package pro.trevor.tankgame.util.function;

public interface IQuadConsumer<T, U, V, W> {
    void accept(T t, U u, V v, W w);
}
