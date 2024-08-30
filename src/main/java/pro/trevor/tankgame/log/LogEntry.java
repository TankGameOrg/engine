package pro.trevor.tankgame.log;

import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class LogEntry extends AttributeContainer {
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
        // The hit_roll attribute is a JSON Object but does not have a class so Codec can't handle it
        return !attribute.equals("hit_roll");
    }
}
