package pro.trevor.tankgame.log;

import java.util.Map;

import org.json.JSONObject;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class LogEntry extends AttributeContainer {
    public LogEntry() {
        super();
    }

    public LogEntry(Map<Attribute<?>, ?> defaults) {
        super(defaults);
    }

    public LogEntry(JSONObject json) {
        super(json);
    }

    @Override
    protected String toAttributeJsonKeyString(String attribute) {
        return attribute.toLowerCase();
    }

    @Override
    protected String toAttributeString(String attributeKey) {
        return attributeKey.toUpperCase();
    }

    @Override
    protected boolean isAttributeJsonKey(String attribute) {
        return true;
    }
}
