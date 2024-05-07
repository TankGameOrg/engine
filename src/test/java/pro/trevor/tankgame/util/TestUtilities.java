package pro.trevor.tankgame.util;

import org.json.JSONObject;

import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.rule.impl.version3.TankAttribute;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;

public class TestUtilities {

    public static Tank buildTestTank(int actions, int gold, boolean dead) {
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

    public static Tank buildDurableTestTank(int actions, int gold, int durability, boolean dead) {
        JSONObject json = new JSONObject();
        JSONObject attributes = new JSONObject();
        attributes.put(TankAttribute.ACTIONS.name(), actions);
        attributes.put(TankAttribute.GOLD.name(), gold);
        attributes.put(TankAttribute.DEAD.name(), dead);
        attributes.put(TankAttribute.DURABILITY.name(), durability);
        json.put("name", "test");
        json.put("attributes", attributes);
        json.put("type", "tank");
        json.put("position", "A1");
        return new Tank(json);
    }

    public static Tank buildPositionedTank(String position, int actions, int gold, int durability, boolean dead) {
        JSONObject json = new JSONObject();
        JSONObject attributes = new JSONObject();
        attributes.put(TankAttribute.ACTIONS.name(), actions);
        attributes.put(TankAttribute.BOUNTY.name(), 0);
        attributes.put(TankAttribute.GOLD.name(), gold);
        attributes.put(TankAttribute.DEAD.name(), dead);
        attributes.put(TankAttribute.DURABILITY.name(), durability);
        attributes.put(TankAttribute.RANGE.name(), 2);
        json.put("name", "test");
        json.put("attributes", attributes);
        json.put("type", "tank");
        json.put("position", position);
        return new Tank(json);
    }

    public static State generateBoard(int width, int height, IUnit... units) {
        Board board = new Board(width, height);
        for (IUnit unit : units) {
            board.putUnit(unit);
        }
        return new State(board, new Council());
    }

    public static Council BuildTestCouncil(int coffer, int councilors, int senators) {
        Council c = new Council(coffer);

        for (int i = 0; i < councilors; i++)
        {
            c.getCouncillors().add("Councilor " + i);
        }

        for (int i = 0; i < senators; i++)
        {
            c.getSenators().add("Senators " + i);
        }

        return c;
    }
}
