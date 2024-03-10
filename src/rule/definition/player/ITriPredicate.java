package rule.definition.player;

public interface ITriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}
