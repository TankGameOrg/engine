package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.attribute.AttributeEntity;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.HashMap;

@JsonType(name = "Player")
public class Player extends AttributeEntity implements IJsonObject {

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
