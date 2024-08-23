package pro.trevor.tankgame.state.board;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.util.JsonType;

import java.util.Map;

@JsonType(name = "GenericElement")
public class GenericElement extends AttributeContainer implements IElement {

    public GenericElement() {
        super();
    }

    public GenericElement(Map<Attribute<?>, Object> defaults) {
        super(defaults);
    }

    public GenericElement(JSONObject json) {
        super(json);
    }

    public Position getPosition() {
        return this.getUnsafe(Attribute.POSITION);
    }

    public void setPosition(Position position) {
        this.put(Attribute.POSITION, position);
    }

    @Override
    public char toBoardCharacter() {
        return '?';
    }
}
