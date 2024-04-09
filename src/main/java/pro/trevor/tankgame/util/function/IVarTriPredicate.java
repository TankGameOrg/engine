package pro.trevor.tankgame.util.function;

public interface IVarTriPredicate<T, U, V> {
    boolean test(T t, U u, V... v);
}
