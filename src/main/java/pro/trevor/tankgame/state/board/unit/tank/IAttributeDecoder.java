package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONObject;

import java.util.Map;

public interface IAttributeDecoder<E extends Enum<E> & IAttribute> extends IDecoder<E, String> {
    Map<E, Object> fromJsonAttributes(JSONObject json);
    E fromSource(String attribute);
}
