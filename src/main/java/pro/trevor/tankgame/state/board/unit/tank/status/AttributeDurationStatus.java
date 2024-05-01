package pro.trevor.tankgame.state.board.unit.tank.status;

import org.json.JSONObject;
import pro.trevor.tankgame.state.board.unit.tank.IAttribute;
import pro.trevor.tankgame.state.board.unit.tank.IAttributeDecoder;

public abstract class AttributeDurationStatus<E extends Enum<E> & IAttribute, T> extends DurationStatus implements IAttributeStatus<E, T> {

    protected E attribute;

    public AttributeDurationStatus(String name, int duration, E attribute) {
        super(name, duration);
        this.attribute = attribute;
    }

    public AttributeDurationStatus(JSONObject json, IAttributeDecoder<E> decoder) {
        super(json);
        attribute = decoder.fromSource(json.getString("attribute"));
    }

    @Override
    public E attributeEffected() {
        return attribute;
    }

    @Override
    public JSONObject toJson() {
        JSONObject output = super.toJson();
        output.put("attribute", attribute.name());
        return output;
    }
}
