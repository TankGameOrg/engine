package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.List;
import java.util.stream.Stream;

@JsonType(name = "Council")
public class Council extends AttributeContainer implements IPlayerElement, IMetaElement, IJsonObject {

    public Council() {
        super();
        this.put(Attribute.COUNCILLORS, new AttributeList<>());
        this.put(Attribute.SENATORS, new AttributeList<>());
    }

    public Council(JSONObject json) {
        super(json);
    }

    public AttributeList<PlayerRef> getCouncillors() {
        return this.getUnsafe(Attribute.COUNCILLORS);
    }

    public AttributeList<PlayerRef> getSenators() {
        return this.getUnsafe(Attribute.SENATORS);
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

    @Override
    public PlayerRef getPlayerRef() {
        return new PlayerRef("Council");
    }
}
