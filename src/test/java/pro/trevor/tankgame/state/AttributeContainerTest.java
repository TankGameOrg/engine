package pro.trevor.tankgame.state;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.attribute.Codec;

public class AttributeContainerTest {

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
        AttributeContainer object = new AttributeContainer(json);
        Assertions.assertEquals(json.toString(), object.toJson().toString());
        Assertions.assertEquals(object, Codec.decodeJson(object.toJson()));
    }

}
