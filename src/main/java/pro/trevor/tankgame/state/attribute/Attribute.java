package pro.trevor.tankgame.state.attribute;

import java.util.Optional;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.Player;

public class Attribute<E> {

    // Element attributes
    public static final Attribute<Position> POSITION = new Attribute<>("POSITION", Position.class);

    // Tank attributes
    public static final Attribute<Integer> GOLD = new Attribute<>("GOLD", Integer.class);
    public static final Attribute<Integer> ACTION_POINTS = new Attribute<>("ACTIONS", Integer.class);
    public static final Attribute<Integer> RANGE = new Attribute<>("RANGE", Integer.class);
    public static final Attribute<Integer> BOUNTY = new Attribute<>("BOUNTY", Integer.class);
    public static final Attribute<Boolean> DEAD = new Attribute<>("DEAD", Boolean.class);
    public static final Attribute<Player> PLAYER = new Attribute<>("PLAYER", Player.class);
    public static final Attribute<Long> TIME_OF_LAST_ACTION = new Attribute<>("TIME_OF_LAST_ACTION", Long.class);

    // Durability attributes
    public static final Attribute<Integer> DURABILITY = new Attribute<>("DURABILITY", Integer.class);
    public static final Attribute<Integer> MAX_DURABILITY = new Attribute<>("MAX_DURABILITY", Integer.class);
    public static final Attribute<Boolean> DESTROYED = new Attribute<>("DESTROYED", Boolean.class);

    // Floor attributes
    public static final Attribute<Integer> REGENERATION = new Attribute<>("REGENERATION", Integer.class);

    // State attributes
    public static final Attribute<Integer> TICK = new Attribute<>("TICK", Integer.class);
    public static final Attribute<Boolean> RUNNING = new Attribute<>("RUNNING", Boolean.class);
    public static final Attribute<String> WINNER = new Attribute<>("WINNER", String.class);
    public static final Attribute<AttributeList> PLAYERS = new Attribute<>("PLAYERS", AttributeList.class); // AttributeList<Player>
    public static final Attribute<Council> COUNCIL = new Attribute<>("COUNCIL", Council.class);
    public static final Attribute<Board> BOARD = new Attribute<>("BOARD", Board.class);

    // Council attributes
    public static final Attribute<AttributeList> COUNCILLORS = new Attribute<>("COUNCILLORS", AttributeList.class); // AttributeList<Player>
    public static final Attribute<AttributeList> SENATORS = new Attribute<>("SENATORS", AttributeList.class); // AttributeList<Player>
    public static final Attribute<Integer> COFFER = new Attribute<>("COFFER", Integer.class);
    public static final Attribute<Boolean> CAN_BOUNTY = new Attribute<>("CAN_BOUNTY", Boolean.class);
    public static final Attribute<Integer> ARMISTICE_COUNT = new Attribute<>("ARMISTICE_COUNT", Integer.class);
    public static final Attribute<Integer> ARMISTICE_MAX = new Attribute<>("ARMISTICE_MAX", Integer.class);

    // Player attributes
    public static final Attribute<String> NAME = new Attribute<>("NAME", String.class);

    private final String attributeName;
    private final Class<E> attributeClass;

    public Attribute(String name, Class<E> attributeClass) {
        this.attributeName = name;
        this.attributeClass = attributeClass;
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

    public E remove(AttributeObject e) {
        return attributeClass.cast(e.remove(attributeName));
    }

    public String getName() {
        return attributeName;
    }

    private enum WrapperClass {
        Long,
        Integer,
        Short,
        Byte,
        Double,
        Float
    }

    private E numberToE(Number number) {
        try {
            WrapperClass wrapper = WrapperClass.valueOf(attributeClass.getSimpleName());
            return attributeClass.cast(switch (wrapper) {
                case Long -> number.longValue();
                case Integer -> number.intValue();
                case Short -> number.shortValue();
                case Byte -> number.byteValue();
                case Double -> number.doubleValue();
                case Float -> number.floatValue();
            });
        } catch (IllegalArgumentException e) {
            throw new Error("Number class " + attributeClass.getSimpleName() + " is not a primitive Number", e);
        }
    }

    private E getObject(AttributeObject e) {
        Object o = e.get(attributeName);
        try {
            return attributeClass.cast(o);
        } catch (ClassCastException exception) {
            if (o instanceof Number number && Number.class.isAssignableFrom(attributeClass)) {
                return numberToE(number);
            }
            throw new Error("Error attempting to get attribute '" + attributeName + "' from generic element " + e
                    + ". Object " + o + " cannot be casted to it's type.", exception);
        }
    }
}
