package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.attribute.Attribute;
import pro.trevor.tankgame.attribute.ListEntity;
import pro.trevor.tankgame.attribute.AttributeEntity;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.List;
import java.util.stream.Stream;

@JsonType(name = "Council")
public class Council extends AttributeEntity implements IJsonObject {

    public Council() {
        super();
        put(Attribute.COUNCILLORS, new ListEntity<>());
        put(Attribute.SENATORS, new ListEntity<>());
    }

    public Council(JSONObject json) {
        super(json);
    }

    public ListEntity<PlayerRef> getCouncillors() {
        return getUnsafe(Attribute.COUNCILLORS);
    }

    public ListEntity<PlayerRef> getSenators() {
        return getUnsafe(Attribute.SENATORS);
    }

    public List<PlayerRef> allPlayersOnCouncil() {
        return Stream.concat(getCouncillors().stream(), getSenators().stream()).toList();
    }

    public boolean isPlayerCouncillor(PlayerRef playerRef) {
        return getCouncillors().stream().anyMatch(playerRef::equals);
    }

    public boolean isPlayerSenator(PlayerRef playerRef) {
        return getSenators().stream().anyMatch(playerRef::equals);
    }

    public boolean isPlayerOnCouncil(PlayerRef playerRef) {
        return allPlayersOnCouncil().stream().anyMatch(playerRef::equals);
    }
}
