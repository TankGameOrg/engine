package pro.trevor.tankgame.state.meta;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.rule.type.ITickElement;

/**
 * A class to represent a (meta) tick element that does not rely on any particular element.
 */
public class None implements ITickElement, IMetaElement {

    @Override
    public JSONObject toJsonObject() {
        return new JSONObject();
    }
}
