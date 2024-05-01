package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.IPositioned;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.board.unit.tank.status.IAttributeStatus;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatus;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatusDecoder;

import java.util.*;

public class GenericTank<E extends Enum<E> & IAttribute> implements IUnit, IMovable, IPositioned, ITickElement, IPlayerElement {

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

    public GenericTank(JSONObject json, Position position, IAttributeDecoder<E> attributeDecoder, IStatusDecoder statusDecoder) {
        assert !json.get("type").equals("tank");

        this.player = json.getString("name");
        this.position = position;
//         this.position = Position.fromJson(json.getJSONObject("position"));
        this.attributes = attributeDecoder.fromJsonAttributes(json.getJSONObject("attributes"));
        this.statuses = new HashSet<>();

        JSONArray statuses = json.getJSONArray("statuses");
        for (int i = 0; i < statuses.length(); ++i) {
            JSONObject statusJson = statuses.getJSONObject(i);
            this.statuses.add(statusDecoder.fromSource(statusJson));
        }
    }

    protected <T> T get(E attribute, Class<T> type) {
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

    protected void set(E attribute, Object object) {
        if (attribute.getType().isAssignableFrom(object.getClass())) {
            attributes.put(attribute, object);
        } else {
            throw new Error(String.format("Attribute %s cannot store a(n) %s", attribute.name(), object.getClass().getSimpleName()));
        }
    }

    public int getInteger(E attribute) {
        int value = get(attribute, Integer.class);
        int modification = 0;
        for (IStatus status : statuses) {
            if (status instanceof IAttributeStatus<?, ?> s && s.attributeEffected().equals(attribute)) {
                IAttributeStatus<E, Integer> castStatus = (IAttributeStatus<E, Integer>) s;
                modification += castStatus.modify(value);
            }
        }
        return value + modification;
    }

    public double getDouble(E attribute) {
        double value = get(attribute, Double.class);
        double modification = 0;
        for (IStatus status : statuses) {
            if (status instanceof IAttributeStatus<?, ?> s && s.attributeEffected().equals(attribute)) {
                IAttributeStatus<E, Double> castStatus = (IAttributeStatus<E, Double>) s;
                modification += castStatus.modify(value);
            }
        }
        return value + modification;
    }

    public boolean getBoolean(E attribute) {
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

    public Set<IStatus> getStatuses() {
        return statuses;
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

        output.put("attributes", attributesJson);

        JSONArray statusesJson = new JSONArray();
        for (IStatus status : statuses) {
            statusesJson.put(status.toJson());
        }

        output.put("statuses", statusesJson);

        return output;
    }

    @Override
    public String toString() {
        return getPlayer();
    }
}
