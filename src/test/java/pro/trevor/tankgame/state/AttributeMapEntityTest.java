package pro.trevor.tankgame.state;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.attribute.AttributeEntity;
import pro.trevor.tankgame.attribute.Codec;

public class AttributeMapEntityTest {

    @Test
    void testAttributeContainerCodec() {
        JSONObject json = new JSONObject(
"""
{
  "$NAME": "Test",
  "$POSITION": {
    "x": 2,
    "y": 2,
    "class": "Position"
  },
  "$ACTIONS": 0,
  "$DEAD": false,
  "class": "AttributeContainer"
}
""");
        AttributeEntity object = new AttributeEntity(json);
        Assertions.assertEquals(json.toString(), object.toJson().toString());
        Assertions.assertEquals(object, Codec.decodeJson(object.toJson()));
    }

}
