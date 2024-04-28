package pro.trevor.tankgame.state.board.unit.tank;

import org.json.JSONObject;

import java.util.Map;

public interface IAttributeDecoder<E extends Enum<E> & IAttribute> {
    Map<E, Object> fromJson(JSONObject json);
    E fromString(String attribute);
}
