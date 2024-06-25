package pro.trevor.tankgame.state;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.attribute.Codec;

public class AttributeObjectTest {

    @Test
    void testAttributeObjectCodec() {
        JSONObject json = new JSONObject(
"""
{
  "attributes": {
    "NAME": "Test",
    "POSITION": {
      "x": 2,
      "y": 2,
      "class": "Position"
    },
    "ACTIONS": 0,
    "DEAD": false,
  },
  "class": "AttributeObject"
}
""");
        AttributeObject object = new AttributeObject(json);
        Assertions.assertEquals(json.toString(), object.toJson().toString());
        Assertions.assertEquals(object, Codec.decodeJson(object.toJson()));
    }

}
