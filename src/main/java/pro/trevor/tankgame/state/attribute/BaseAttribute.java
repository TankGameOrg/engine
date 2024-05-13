package pro.trevor.tankgame.state.attribute;

import java.util.Optional;

import pro.trevor.tankgame.state.board.GenericElement;

public abstract class BaseAttribute<E> {

    public boolean in(GenericElement e) {
        return e.has(getName());
    }

    public Optional<E> from(GenericElement e) {
        return Optional.ofNullable(getObject(e));
    }

    public E unsafeFrom(GenericElement e) {
        if (!this.in(e))
            throw new Error("Attempting to get attribute '" + getName() + "' from generic element " + e
                    + ". This generic element has no such attribute");

        return getObject(e);
    }

    @SuppressWarnings("unchecked")
    private E getObject(GenericElement e) {
        Object o = e.get(getName());
        try {
            return (E) o;
        } catch (ClassCastException exception) {
            throw new Error(
                    "Attempting to get attribute '" + getName() + "' from generic element " + e + "\nObject " + o
                            + " cannot be casted to type " + getType() + ".",
                    exception);
        }
    }

    public void to(GenericElement e, E o) {
        e.set(getName(), o);
    }

    protected abstract String getName();

    protected abstract String getType();
}
