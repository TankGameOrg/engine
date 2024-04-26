package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.IPositioned;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatus;

import java.util.*;

public class GenericTank<E extends Enum<E> & IAttribute> implements IMovable, IPositioned, ITickElement, IPlayerElement {

    protected final String player;
    protected Position position;
    protected final Map<E, Object> attributes;
    protected final Set<IStatus> statuses;

    public GenericTank(String player, Position position, Map<E, Object> defaults) {
        this.player = player;
        this.position = position;
        this.attributes = new HashMap<>();
        this.statuses = new HashSet<>();
        for (E attribute : defaults.keySet()) {
            attributes.put(attribute, defaults.get(attribute));
        }
    }

    <T> T get(E attribute, Class<T> type) {
        if (attribute.getType().isAssignableFrom(type)) {
            try {
                return type.cast(attributes.get(attribute));
            } catch (ClassCastException ignored) {
                throw new Error(String.format("Attribute %s was not stored as a(n) %s", attribute.name(), type.getSimpleName()));
            }
        } else {
            throw new Error(String.format("Unable to read attribute %s as a(n) %s", attribute.name(), type.getSimpleName()));
        }
    }

    void set(E attribute, Object object) {
        if (attribute.getType().isAssignableFrom(object.getClass())) {
            attributes.put(attribute, object);
        } else {
            throw new Error(String.format("Attribute %s cannot store a(n) %s", attribute.name(), object.getClass().getSimpleName()));
        }
    }

    int getInteger(E attribute) {
        return get(attribute, Integer.class);
    }

    double getDouble(E attribute) {
        return get(attribute, Double.class);
    }

    boolean getBoolean(E attribute) {
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

        for (E attribute : attributes.keySet()) {
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
