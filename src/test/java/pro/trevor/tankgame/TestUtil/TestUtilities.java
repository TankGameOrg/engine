package pro.trevor.tankgame.TestUtil;

import org.json.JSONObject;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.rule.impl.version3.TankAttribute;

public class TestUtilities 
{
    public static Tank BuildTestTank(int actions, int gold, boolean dead)
    {
        JSONObject json = new JSONObject();
        JSONObject attributes = new JSONObject();
        attributes.put(TankAttribute.ACTIONS.name(), actions);
        attributes.put(TankAttribute.GOLD.name(), gold);
        attributes.put(TankAttribute.DEAD.name(), dead);
        json.put("name", "test");
        json.put("attributes", attributes);
        json.put("type", "tank");
        json.put("position", "A1");
        return new Tank(json);
    }
}
