package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;

@JsonType(name = "Player")
public class Player extends AttributeContainer implements IJsonObject, IMetaElement {

    public Player(String name) {
        super(new HashMap<>());
        put(Attribute.NAME, name);
    }

    public Player(JSONObject json) {
        super(json);
    }

    public String getName() {
        return getUnsafe(Attribute.NAME);
    }

    public PlayerRef toRef() {
        return new PlayerRef(getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Player other)) return false;
        return getName().equals(other.getName());
    }
}
