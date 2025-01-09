package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.attribute.AttributeEntity;
import pro.trevor.tankgame.util.JsonType;
import pro.trevor.tankgame.util.Position;

import java.util.Map;

@JsonType(name = "Element")
public class Element extends AttributeEntity implements IElement {

    public Element() {
        super();
    }

    public Element(Map<Attribute<?>, Object> defaults) {
        super(defaults);
    }

    public Element(JSONObject json) {
        super(json);
    }

    public Position getPosition() {
        return getUnsafe(Attribute.POSITION);
    }

    public void setPosition(Position position) {
        put(Attribute.POSITION, position);
    }

    @Override
    public char toBoardCharacter() {
        return '?';
    }
}
