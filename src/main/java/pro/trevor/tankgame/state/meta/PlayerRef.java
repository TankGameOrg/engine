package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Codec;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.Objects;
import java.util.Optional;

@JsonType(name = "PlayerRef")
public class PlayerRef implements IJsonObject {

    private final String name;

    public PlayerRef(String name) {
        this.name = name;
    }

    public PlayerRef(JSONObject json) {
        this.name = json.getString("name");
    }

    public String getName() {
        return name;
    }

    public Optional<Player> toPlayer(State state) {
        for (Player p : state.getPlayers()) {
            if (p.getName().equals(name)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("class", Codec.typeFromClass(getClass()));
        json.put("name", name);
        return json;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PlayerRef playerRef)) return false;
        return Objects.equals(name, playerRef.name);
    }
}
