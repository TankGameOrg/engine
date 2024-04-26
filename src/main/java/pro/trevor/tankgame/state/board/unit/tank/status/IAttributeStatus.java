package pro.trevor.tankgame.state.board.unit.tank.status;

import pro.trevor.tankgame.state.board.unit.tank.IAttribute;

/**
 * A status that applies to a tank and modifies one of its attributes.
 *
 * @param <E> the attribute enum class.
 * @param <T> the type corresponding to the given attribute.
 */
public interface IAttributeStatus<E extends Enum<E> & IAttribute, T> {
    E attributeEffected();
    T modify(T base);
}
