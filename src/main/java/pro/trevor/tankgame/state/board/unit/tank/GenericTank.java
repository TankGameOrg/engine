package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONArray;
import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.rule.type.ITickElement;
import pro.trevor.tankgame.state.board.IMovable;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericUnit;
import pro.trevor.tankgame.state.board.unit.tank.status.IAttributeStatus;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatus;
import pro.trevor.tankgame.state.board.unit.tank.status.IStatusDecoder;

import java.util.*;

public class GenericTank<E extends Enum<E> & IAttribute> extends GenericUnit<E> implements IMovable, ITickElement, IPlayerElement {

    protected final String player;
    protected final Set<IStatus> statuses;

    public GenericTank(String player, Position position, Map<E, Object> defaults) {
        super(position, defaults);
        this.player = player;
        this.statuses = new HashSet<>();
    }

    public GenericTank(JSONObject json, IAttributeDecoder<E> attributeDecoder, IStatusDecoder statusDecoder) {
        super(json, attributeDecoder);
        this.player = json.getString("name");
        this.statuses = new HashSet<>();
        JSONArray statusesJson = json.getJSONArray("statuses");
        for (int i = 0; i < statusesJson.length(); ++i) {
            JSONObject statusJson = statusesJson.getJSONObject(i);
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

    public int getIntegerWithModifiers(E attribute) {
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

    public double getDoubleWithModifiers(E attribute) {
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

    @Override
    public String getPlayer() {
        return player;
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
        JSONObject output = super.toJson();

        output.put("type", "tank");
        output.put("name", player);

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
