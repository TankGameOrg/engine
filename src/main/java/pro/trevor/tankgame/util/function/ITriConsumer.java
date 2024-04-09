package pro.trevor.tankgame.util.function;

public interface ITriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
