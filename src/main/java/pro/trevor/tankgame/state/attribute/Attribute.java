package pro.trevor.tankgame.state.attribute;

import java.util.Arrays;
import java.util.Optional;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;

public class Attribute<E> {

    // Element attributes
    public static final Attribute<Position> POSITION = new Attribute<>("POSITION");

    // Tank attributes
    public static final Attribute<Integer> GOLD = new Attribute<>("GOLD");
    public static final Attribute<Integer> ACTION_POINTS = new Attribute<>("ACTIONS");
    public static final Attribute<Integer> RANGE = new Attribute<>("RANGE");
    public static final Attribute<Integer> BOUNTY = new Attribute<>("BOUNTY");
    public static final Attribute<Boolean> DEAD = new Attribute<>("DEAD");
    public static final Attribute<Player> PLAYER = new Attribute<>("PLAYER");
    public static final Attribute<Long> TIME_OF_LAST_ACTION = new Attribute<>("TIME_OF_LAST_ACTION");

    // Durability attributes
    public static final Attribute<Integer> DURABILITY = new Attribute<>("DURABILITY");
    public static final Attribute<Integer> MAX_DURABILITY = new Attribute<>("MAX_DURABILITY");
    public static final Attribute<Boolean> DESTROYED = new Attribute<>("DESTROYED");

    // Floor attributes
    public static final Attribute<Integer> REGENERATION = new Attribute<>("REGENERATION");

    // State attributes
    public static final Attribute<Boolean> TICK = new Attribute<>("TICK");
    public static final Attribute<Boolean> RUNNING = new Attribute<>("RUNNING");
    public static final Attribute<String> WINNER = new Attribute<>("WINNER");
    public static final Attribute<Council> COUNCIL = new Attribute<>("COUNCIL");
    public static final Attribute<Board> BOARD = new Attribute<>("BOARD");

    // Council attributes
    public static final Attribute<Integer> ARMISTICE_COUNT = new Attribute<>("ARMISTICE_COUNT");
    public static final Attribute<Integer> ARMISTICE_MAX = new Attribute<>("ARMISTICE_MAX");

    // Player attributes
    public static final Attribute<String> NAME = new Attribute<>("NAME");

    private final String attributeName;

    public Attribute(String name) {
        this.attributeName = name;
    }

    public boolean in(AttributeObject e) {
        return e.has(attributeName);
    }

    public Optional<E> from(AttributeObject e) {
        return Optional.ofNullable(getObject(e));
    }

    public E fromOrElse(AttributeObject e, E defaultValue) {
        if (in(e)) {
            return getObject(e);
        } else {
            return defaultValue;
        }
    }

    public E unsafeFrom(AttributeObject e) {
        if (!this.in(e))
            throw new Error("Attempting to get attribute '" + attributeName + "' from generic element " + e
                    + ". This generic element has no such attribute");

        return getObject(e);
    }
        
    public void to(AttributeObject e, E o) {
        e.set(attributeName, o);
    }

    public void toIfNotPresent(AttributeObject e, E o) {
        if (!in(e)) {
            e.set(attributeName, o);
        }
    }

    public String getName() {
        return attributeName;
    }

    @SuppressWarnings("unchecked")
    private E getObject(AttributeObject e) {
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
}
