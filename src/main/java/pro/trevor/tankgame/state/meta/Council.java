package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

import java.util.List;
import java.util.stream.Stream;

@JsonType(name = "Council")
public class Council extends AttributeObject implements IPlayerElement, IMetaElement, IJsonObject {

    public Council() {
        super();
        Attribute.COUNCILLORS.to(this, new AttributeList<>());
        Attribute.SENATORS.to(this, new AttributeList<>());
    }

    public Council(JSONObject json) {
        super(json);
    }

    public AttributeList<PlayerRef> getCouncillors() {
        return Attribute.COUNCILLORS.unsafeFrom(this);
    }

    public AttributeList<PlayerRef> getSenators() {
        return Attribute.SENATORS.unsafeFrom(this);
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
