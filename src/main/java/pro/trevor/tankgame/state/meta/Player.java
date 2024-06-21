package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;

@JsonType(name = "Player")
public class Player extends AttributeObject implements IJsonObject {

    public Player(String name) {
        super(new HashMap<>());
        Attribute.NAME.to(this, name);
    }

    public Player(JSONObject json) {
        super(json);
    }

    public String getName() {
        return Attribute.NAME.unsafeFrom(this);
    }
}
