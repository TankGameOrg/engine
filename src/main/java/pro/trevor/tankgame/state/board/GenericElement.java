package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;

import java.util.Map;

public abstract class GenericElement extends AttributeObject implements IElement {

    public GenericElement() {
        super();
    }

    public GenericElement(Map<String, Object> defaults) {
        super(defaults);
    }

    public GenericElement(JSONObject json) {
        super(json);
    }

    public Position getPosition() {
        return Attribute.POSITION.unsafeFrom(this);
    }

    public void setPosition(Position position) {
        Attribute.POSITION.to(this, position);
    }
}
