package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeList;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.JsonType;

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

    public AttributeList<Player> getCouncillors() {
        return Attribute.COUNCILLORS.unsafeFrom(this);
    }

    public AttributeList<Player> getSenators() {
        return Attribute.SENATORS.unsafeFrom(this);
    }

    @Override
    public Player getPlayer() {
        return new Player("Council");
    }
}
