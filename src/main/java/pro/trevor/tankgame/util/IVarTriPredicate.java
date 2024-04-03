package pro.trevor.tankgame.util;

public interface IVarTriPredicate<T, U, V> {
    boolean test(T t, U u, V... v);
}
