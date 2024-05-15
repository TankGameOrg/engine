package pro.trevor.tankgame.state.attribute;

import java.util.Optional;

import pro.trevor.tankgame.state.board.GenericElement;

public class Attribute<E> {

    public static Attribute<Integer> GOLD = new Attribute<Integer>("GOLD");
    public static Attribute<Integer> ACTION_POINTS = new Attribute<Integer>("ACTION_POINTS");
    public static Attribute<Integer> DURABILITY = new Attribute<Integer>("DURABILITY");
    public static Attribute<Integer> RANGE = new Attribute<Integer>("RANGE");
    public static Attribute<Integer> BOUNTY = new Attribute<Integer>("BOUNTY");
    public static Attribute<Boolean> DEAD = new Attribute<Boolean>("DEAD");

    private String attributeName;

    public Attribute(String name) {
        this.attributeName = name;
    }

    public boolean in(GenericElement e) {
        return e.has(attributeName);
    }

    public Optional<E> from(GenericElement e) {
        return Optional.ofNullable(getObject(e));
    }

    public E unsafeFrom(GenericElement e) {
        if (!this.in(e))
            throw new Error("Attempting to get attribute '" + attributeName + "' from generic element " + e
                    + ". This generic element has no such attribute");

        return getObject(e);
    }

    @SuppressWarnings("unchecked")
    private E getObject(GenericElement e) {
        Object o = e.get(attributeName);
        try {
            return (E) o;
        } catch (ClassCastException exception) {
            throw new Error(
                    "Attempting to get attribute '" + attributeName + "' from generic element " + e + "\nObject " + o
                            + " cannot be casted to it's type.",
                    exception);
        }
    }

    public void to(GenericElement e, E o) {
        e.set(attributeName, o);
    }

    public String getName() {
        return attributeName;
    }
}
