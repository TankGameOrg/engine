package rule.impl.player;

public interface ITriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}
