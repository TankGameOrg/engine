package pro.trevor.tankgame.state.attribute;

import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class Attribute<E> {

    // Element attributes
    public static final Attribute<Position> POSITION = new Attribute<>("POSITION", Position.class);
    public static final Attribute<Integer> DAYS_REMAINING = new Attribute<>("DAYS_REMAINING", Integer.class);

    // Tank attributes
    public static final Attribute<PlayerRef> PLAYER_REF = new Attribute<>("PLAYER_REF", PlayerRef.class);
    public static final Attribute<Integer> GOLD = new Attribute<>("GOLD", Integer.class);
    public static final Attribute<Integer> ACTION_POINTS = new Attribute<>("ACTIONS", Integer.class);
    public static final Attribute<Integer> MAX_ACTION_POINTS = new Attribute<>("MAX_ACTIONS", Integer.class);
    public static final Attribute<Integer> RANGE = new Attribute<>("RANGE", Integer.class);
    public static final Attribute<Integer> SPEED = new Attribute<>("SPEED", Integer.class);
    public static final Attribute<Integer> PREVIOUS_SPEED = new Attribute<>("PREVIOUS_SPEED", Integer.class);
    public static final Attribute<Integer> BOUNTY = new Attribute<>("BOUNTY", Integer.class);
    public static final Attribute<Boolean> DEAD = new Attribute<>("DEAD", Boolean.class);
    public static final Attribute<Boolean> SLOWED = new Attribute<>("SLOWED", Boolean.class);
    public static final Attribute<Boolean> HASTENED = new Attribute<>("HASTENED", Boolean.class);

    // Dead tank attributes
    public static final Attribute<PlayerRef> ONLY_LOOTABLE_BY = new Attribute<>("ONLY_LOOTABLE_BY", PlayerRef.class);
    public static final Attribute<Boolean> PLAYER_CAN_LOOT = new Attribute<>("PLAYER_CAN_LOOT", Boolean.class);

    // Durability attributes
    public static final Attribute<Integer> DURABILITY = new Attribute<>("DURABILITY", Integer.class);
    public static final Attribute<Integer> MAX_DURABILITY = new Attribute<>("MAX_DURABILITY", Integer.class);
    public static final Attribute<Boolean> DESTROYED = new Attribute<>("DESTROYED", Boolean.class);

    // LootBox attributes
    public static Attribute<Boolean> HAS_BEEN_LOOTED = new Attribute<>("HAS_BEEN_LOOTED", Boolean.class);

    // Floor attributes
    public static final Attribute<Integer> REGENERATION = new Attribute<>("REGENERATION", Integer.class);
    public static final Attribute<Integer> DAMAGE = new Attribute<>("DAMAGE", Integer.class);

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
    public static final Attribute<String> TEAM = new Attribute<>("TEAM", String.class);
    public static final Attribute<Integer> POWER = new Attribute<>("POWER", Integer.class);
    public static final Attribute<Long> GLOBAL_COOLDOWN_END_TIME = new Attribute<>("GLOBAL_COOLDOWN_END_TIME", Long.class);

    // Log entry attributes
    public static final Attribute<Position> TARGET_POSITION = new Attribute<>("TARGET", Position.class);
    public static final Attribute<PlayerRef> TARGET_PLAYER = new Attribute<>("TARGET", PlayerRef.class);
    public static final Attribute<Integer> DONATION = new Attribute<>("DONATION", Integer.class);
    public static final Attribute<Boolean> HIT = new Attribute<>("HIT", Boolean.class);
    public static final Attribute<Integer> DAMAGAE = new Attribute<>("DAMAGAE", Integer.class);
    public static final Attribute<Long> TIMESTAMP = new Attribute<>("TIMESTAMP", Long.class);
    public static final Attribute<Integer> DAY = new Attribute<>("DAY", Integer.class);
    public static final Attribute<PlayerRef> SUBJECT = new Attribute<>("SUBJECT", PlayerRef.class);
    public static final Attribute<String> ACTION = new Attribute<>("ACTION", String.class);

    private final String attributeName;
    private final Class<E> attributeClass;

    public Attribute(String name, Class<E> attributeClass) {
        this.attributeName = name;
        this.attributeClass = attributeClass;
    }

    public String getName() {
        return attributeName;
    }

    public Class<E> getAttributeClass() {
        return attributeClass;
    }
}
