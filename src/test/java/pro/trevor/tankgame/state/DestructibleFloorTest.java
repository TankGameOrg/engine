package pro.trevor.tankgame.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.DestructibleFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestUtilities;

public class DestructibleFloorTest {

    private DestructibleFloor GetTestFloor(Position p, int durability, int maxDurability)
    {
        assert durability >= 0;
        JSONObject json = new JSONObject();
        json.put("type", "destructible_floor");

        JSONObject attributes = new JSONObject();
        attributes.put(Attribute.DURABILITY.getName(), durability);
        attributes.put(Attribute.MAX_DURABILITY.getName(), maxDurability);
        if (durability == 0) attributes.put(Attribute.DESTROYED.getName(), true);
        json.put("attributes", attributes);

        return new DestructibleFloor(p, json);
    }

    @Test
    void testGetPosition() {
        DestructibleFloor floor = GetTestFloor(new Position("A1"), 3, 3);

        Position p = floor.getPosition();

        assertEquals("A1", p.toBoardString());
    }

    @Test
    void testIsWalkable() {
        DestructibleFloor floor = GetTestFloor(new Position("A1"), 1, 3);
        DestructibleFloor brokeFloor = GetTestFloor(new Position("A1"), 0, 3);

        assertTrue(floor.isWalkable(null));
        assertFalse(brokeFloor.isWalkable(null));
    }

    @Test
    void testUnbrokenToJson() {
        DestructibleFloor floor = GetTestFloor(new Position("A1"), 1, 3);

        JSONObject json = floor.toJson();
        DestructibleFloor newFloor = new DestructibleFloor(new Position("A1"), json);

        assertEquals("destructible_floor", json.getString("type"));
        assertEquals(floor.getPosition(), newFloor.getPosition());
        assertEquals(Attribute.DURABILITY.unsafeFrom(floor), Attribute.DURABILITY.unsafeFrom(newFloor));
        assertEquals(Attribute.MAX_DURABILITY.unsafeFrom(floor), Attribute.MAX_DURABILITY.unsafeFrom(newFloor));
    }

    @Test
    void testBrokenToJson() {
        DestructibleFloor brokenFloor = GetTestFloor(new Position("A1"), 0, 3);

        JSONObject json = brokenFloor.toJson();
        DestructibleFloor newFloor = new DestructibleFloor(new Position("A1"), json);

        assertEquals("destructible_floor", json.getString("type"));
        assertEquals(brokenFloor.getPosition(), newFloor.getPosition());
        assertEquals(Attribute.DURABILITY.unsafeFrom(brokenFloor), Attribute.DURABILITY.unsafeFrom(newFloor));
        assertEquals(Attribute.MAX_DURABILITY.unsafeFrom(brokenFloor), Attribute.MAX_DURABILITY.unsafeFrom(newFloor));
        assertEquals(Attribute.DESTROYED.unsafeFrom(brokenFloor), Attribute.DESTROYED.unsafeFrom(newFloor));
    }

    @Test
    void shootFloorTest()
    {
        Tank t = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 3, 3);
        s.getBoard().putFloor(floor);

        PlayerActionRule<Tank> shootRule = PlayerRules.SHOOT_V4;
        boolean canShoot = shootRule.canApply(s, t, new Position("B1"), true);
        shootRule.apply(s, t, new Position("B1"), true);

        assertTrue(canShoot);
        assertEquals(2, Attribute.DURABILITY.unsafeFrom(floor));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void destroyFloorTest()
    {
        Tank t = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 1, 3);
        s.getBoard().putFloor(floor);

        PlayerActionRule<Tank> shootRule = PlayerRules.SHOOT_V4;
        boolean canShoot = shootRule.canApply(s, t, new Position("B1"), true);
        shootRule.apply(s, t, new Position("B1"), true);

        assertTrue(canShoot);
        assertEquals(0, Attribute.DURABILITY.unsafeFrom(floor));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void walkAcrossThenDestroyFloorTest()
    {
        Tank t = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 1, 3);
        s.getBoard().putFloor(floor);
        PlayerActionRule<Tank> shootRule = PlayerRules.SHOOT_V4;
        PlayerActionRule<Tank> moveRule = PlayerRules.GetMoveRule(Attribute.ACTION_POINTS, 1);

        // Move onto destructible floor, then move past it
        moveRule.apply(s, t, new Position("B1"));
        assertEquals(t.getPosition(), floor.getPosition());
        moveRule.apply(s, t, new Position("C1"));

        // Shoot at the destructible floor, destroying it
        shootRule.apply(s, t, new Position("B1"), true);
        assertEquals(0, Attribute.DURABILITY.unsafeFrom(floor));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));

        // Try to move onto the broken floor, you cannot
        assertFalse(moveRule.canApply(s, t, new Position("B1")));
        assertThrows(Error.class, () -> moveRule.apply(s, t, new Position("B1")));
    }

    @Test
    void shootUnitAboveFloorTest()
    {
        Tank t = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).with(Attribute.GOLD, 0).at(new Position("A1")).finish();
        Tank tankAbove = TankBuilder.buildV3Tank().with(Attribute.DURABILITY, 1).with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.GOLD, 0).with(Attribute.BOUNTY, 0).at(new Position("B1")).finish();

        State s = TestUtilities.generateBoard(3, 1, t, tankAbove);
        int initialFloorDurability = 1;
        DestructibleFloor floor = GetTestFloor(new Position("B1"), initialFloorDurability, 3);
        s.getBoard().putFloor(floor);

        PlayerActionRule<Tank> shootRule = PlayerRules.SHOOT_V4;
        ConditionalRule<Tank> dieOrDestroyRule = ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule();
        shootRule.apply(s, t, new Position("B1"), true);
        dieOrDestroyRule.apply(s, t);
        dieOrDestroyRule.apply(s, tankAbove);

        // Floor durability is unchanged
        assertEquals(initialFloorDurability, Attribute.DURABILITY.unsafeFrom(floor));
        assertTrue(Attribute.DEAD.unsafeFrom(tankAbove));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void destroyUnitAboveFloorTest()
    {
        Tank t = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).with(Attribute.GOLD, 0).at(new Position("A1")).finish();
        BasicWall wall = new BasicWall(new Position("B1"), 1);

        State s = TestUtilities.generateBoard(3, 1, t, wall);
        int initialFloorDurability = 1;
        DestructibleFloor floor = GetTestFloor(new Position("B1"), initialFloorDurability, 3);
        s.getBoard().putFloor(floor);
        
        PlayerActionRule<Tank> shootRule = PlayerRules.SHOOT_V4;
        ConditionalRule<BasicWall> destroyWallRule = ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY;
        PlayerActionRule<Tank> moveRule = PlayerRules.GetMoveRule(Attribute.ACTION_POINTS, 1);
        
        // Shoot once, destroying the wall
        shootRule.apply(s, t, new Position("B1"), true);
        destroyWallRule.apply(s, wall);

        // Floor durability is unchanged
        assertEquals(initialFloorDurability, Attribute.DURABILITY.unsafeFrom(floor));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));

        //Shoot again
        shootRule.apply(s, t, new Position("B1"), true);

        // Floor is destroyed
        assertEquals(0, Attribute.DURABILITY.unsafeFrom(floor));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));

        // Try to move onto the broken floor, you cannot
        assertFalse(moveRule.canApply(s, t, new Position("B1")));
        assertThrows(Error.class, () -> moveRule.apply(s, t, new Position("B1")));
    }
}
