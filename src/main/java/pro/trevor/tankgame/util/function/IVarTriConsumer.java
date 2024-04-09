package pro.trevor.tankgame.util.function;

public interface IVarTriConsumer<T, U, V> {
    void accept(T t, U u, V... v);
}
