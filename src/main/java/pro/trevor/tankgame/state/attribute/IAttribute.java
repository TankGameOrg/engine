package pro.trevor.tankgame.state.attribute;

import pro.trevor.tankgame.state.board.GenericElement;

public interface IAttribute<E> {
    public boolean in(GenericElement e);
    public E from(GenericElement e);
    public void to(GenericElement e, E o);
}
