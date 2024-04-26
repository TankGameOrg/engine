package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.IPositioned;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OpenTank implements IMovable, IPositioned, ITickElement, IPlayerElement {

    public enum Attribute {
        DEAD(Boolean.class),
        DURABILITY(Integer.class),
        MAX_DURABILITY(Integer.class),
        ACTIONS(Integer.class),
        ACTIONS_PER_DAY(Integer.class),
        MAX_ACTIONS(Integer.class),
        RANGE(Integer.class),
        GOLD(Integer.class),
        MAX_GOLD(Integer.class),
        MOVE_SPEED(Integer.class),
        HIT_CHANCE(Double.class),
        ATTACK_DAMAGE(Integer.class);

        private final Class<?> type;

        Attribute(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }

        public boolean isType(Class<?> type) {
            return this.type.isAssignableFrom(type);
        }
    }

    protected final String player;
    protected Position position;
    protected final Map<Attribute, Object> attributes;
    protected final Set<IStatus> statuses;

    public OpenTank(String player, Position position, Map<Attribute, Object> defaults) {
        this.player = player;
        this.position = position;
        this.attributes = new HashMap<>();
        this.statuses = new HashSet<>();
        for (Attribute attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    <T> T get(Attribute attribute, Class<T> type) {
        if (attribute.isType(type)) {
            try {
                return type.cast(attributes.get(attribute));
            } catch (ClassCastException ignored) {
                throw new Error(String.format("Attribute %s was not stored as a(n) %s", attribute.name(), type.getSimpleName()));
            }
        } else {
            throw new Error(String.format("Unable to read attribute %s as a(n) %s", attribute.name(), type.getSimpleName()));
        }
    }

    void set(Attribute attribute, Object object) {
        if (attribute.isType(object.getClass())) {
            attributes.put(attribute, object);
        } else {
            throw new Error(String.format("Attribute %s cannot store a(n) %s", attribute.name(), object.getClass().getSimpleName()));
        }
    }

    int getInteger(Attribute attribute) {
        return get(attribute, Integer.class);
    }

    double getDouble(Attribute attribute) {
        return get(attribute, Double.class);
    }

    boolean getBoolean(Attribute attribute) {
        return get(attribute, Boolean.class);
    }

    @Override
    public String getPlayer() {
        return player;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public char toBoardCharacter() {
        return 'T';
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = new JSONObject();

        output.put("type", "tank");
        output.put("name", player);
        output.put("position", position.toJson());

        JSONObject attributesJson = new JSONObject();

        for (Attribute attribute : attributes.keySet()) {
            String attributeName = attribute.name();
            Object value = attributes.get(attribute);
            switch (value) {
                case Boolean v -> attributesJson.put(attributeName, v);
                case Integer v -> attributesJson.put(attributeName, v);
                case Double v -> attributesJson.put(attributeName, v);
                default -> throw new Error(String.format("Unhandled type %s for attribute %s", attribute.getType().getSimpleName(), attributeName));
            }
        }

        output.put("attributes", attributes);

        JSONArray statusesJson = new JSONArray();

        for (IStatus status : statuses) {
            statusesJson.put(status.toJson());
        }

        return output;
    }

}
