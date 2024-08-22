package pro.trevor.tankgame.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.ConditionalRule;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.DestructibleFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestUtilities;

public class DestructibleFloorTest {

    private DestructibleFloor GetTestFloor(Position p, int durability, int maxDurability)
    {
        assert durability >= 0;
        JSONObject json = new JSONObject();

        json.put(Attribute.DURABILITY.getJsonName(), durability);
        json.put(Attribute.MAX_DURABILITY.getJsonName(), maxDurability);
        json.put(Attribute.POSITION.getJsonName(), p.toJson());
        if (durability == 0) json.put(Attribute.DESTROYED.getJsonName(), true);

        return new DestructibleFloor(json);
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
        DestructibleFloor newFloor = new DestructibleFloor(json);

        assertEquals(floor.getPosition(), newFloor.getPosition());
        assertEquals(floor.getUnsafe(Attribute.DURABILITY), newFloor.getUnsafe(Attribute.DURABILITY));
        assertEquals(floor.getUnsafe(Attribute.MAX_DURABILITY), newFloor.getUnsafe(Attribute.MAX_DURABILITY));
    }

    @Test
    void testBrokenToJson() {
        DestructibleFloor brokenFloor = GetTestFloor(new Position("A1"), 0, 3);

        JSONObject json = brokenFloor.toJson();
        DestructibleFloor newFloor = new DestructibleFloor(json);

        assertEquals(brokenFloor.getPosition(), newFloor.getPosition());
        assertEquals(brokenFloor.getUnsafe(Attribute.DURABILITY), newFloor.getUnsafe(Attribute.DURABILITY));
        assertEquals(brokenFloor.getUnsafe(Attribute.MAX_DURABILITY), newFloor.getUnsafe(Attribute.MAX_DURABILITY));
        assertEquals(brokenFloor.getUnsafe(Attribute.DESTROYED), newFloor.getUnsafe(Attribute.DESTROYED));
    }

    @Test
    void shootFloorTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 3, 3);
        s.getBoard().putFloor(floor);

        IPlayerRule shootRule = PlayerRules.SHOOT_V4;
        boolean canShoot = shootRule.canApply(s, t.getPlayerRef(), new Position("B1"), true);
        shootRule.apply(s, t.getPlayerRef(), new Position("B1"), true);

        assertTrue(canShoot);
        assertEquals(2, floor.getUnsafe(Attribute.DURABILITY));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void destroyFloorTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 1, 3);
        s.getBoard().putFloor(floor);

        IPlayerRule shootRule = PlayerRules.SHOOT_V4;
        boolean canShoot = shootRule.canApply(s, new PlayerRef("test"), new Position("B1"), true);
        shootRule.apply(s, t.getPlayerRef(), new Position("B1"), true);

        assertTrue(canShoot);
        assertEquals(0, floor.getUnsafe(Attribute.DURABILITY));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void walkAcrossThenDestroyFloorTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A1")).finish();
        State s = TestUtilities.generateBoard(3, 1, t);
        DestructibleFloor floor = GetTestFloor(new Position("B1"), 1, 3);
        s.getBoard().putFloor(floor);
        IPlayerRule shootRule = PlayerRules.SHOOT_V4;
        IPlayerRule moveRule = PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1);

        // Move onto destructible floor, then move past it
        moveRule.apply(s, new PlayerRef("test"), new Position("B1"));
        assertEquals(t.getPosition(), floor.getPosition());
        moveRule.apply(s, new PlayerRef("test"), new Position("C1"));

        // Shoot at the destructible floor, destroying it
        shootRule.apply(s, new PlayerRef("test"), new Position("B1"), true);
        assertEquals(0, floor.getUnsafe(Attribute.DURABILITY));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));

        // Try to move onto the broken floor, you cannot
        assertFalse(moveRule.canApply(s, new PlayerRef("test"), new Position("B1")));
        assertThrows(Error.class, () -> moveRule.apply(s, new PlayerRef("test"), new Position("B1")));
    }

    @Test
    void shootUnitAboveFloorTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).with(Attribute.GOLD, 0).at(new Position("A1")).finish();
        GenericTank tankAbove = TankBuilder.buildTank().with(Attribute.DURABILITY, 1).with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.GOLD, 0).with(Attribute.BOUNTY, 0).at(new Position("B1")).finish();

        State s = TestUtilities.generateBoard(3, 1, t, tankAbove);
        int initialFloorDurability = 1;
        DestructibleFloor floor = GetTestFloor(new Position("B1"), initialFloorDurability, 3);
        s.getBoard().putFloor(floor);

        IPlayerRule shootRule = PlayerRules.SHOOT_V4;
        ConditionalRule<GenericTank> dieOrDestroyRule = ConditionalRules.GetKillOrDestroyTankOnZeroDurabilityRule();
        shootRule.apply(s, new PlayerRef("test"), new Position("B1"), true);
        dieOrDestroyRule.apply(s, t);
        dieOrDestroyRule.apply(s, tankAbove);

        // Floor durability is unchanged
        assertEquals(initialFloorDurability, floor.getUnsafe(Attribute.DURABILITY));
        assertTrue(tankAbove.getUnsafe(Attribute.DEAD));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));
    }

    @Test
    void destroyUnitAboveFloorTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).with(Attribute.GOLD, 0).at(new Position("A1")).finish();
        BasicWall wall = new BasicWall(new Position("B1"), 1);

        State s = TestUtilities.generateBoard(3, 1, t, wall);
        int initialFloorDurability = 1;
        DestructibleFloor floor = GetTestFloor(new Position("B1"), initialFloorDurability, 3);
        s.getBoard().putFloor(floor);

        IPlayerRule shootRule = PlayerRules.SHOOT_V4;
        ConditionalRule<BasicWall> destroyWallRule = ConditionalRules.DESTROY_WALL_ON_ZERO_DURABILITY;
        IPlayerRule moveRule = PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1);

        // Shoot once, destroying the wall
        shootRule.apply(s, new PlayerRef("test"), new Position("B1"), true);
        destroyWallRule.apply(s, wall);

        // Floor durability is unchanged
        assertEquals(initialFloorDurability, floor.getUnsafe(Attribute.DURABILITY));
        assertFalse(Attribute.DESTROYED.from(floor).orElse(false));

        //Shoot again
        shootRule.apply(s, new PlayerRef("test"), new Position("B1"), true);

        // Floor is destroyed
        assertEquals(0, floor.getUnsafe(Attribute.DURABILITY));
        assertTrue(Attribute.DESTROYED.from(floor).orElse(false));

        // Try to move onto the broken floor, you cannot
        assertFalse(moveRule.canApply(s, new PlayerRef("test"), new Position("B1")));
        assertThrows(Error.class, () -> moveRule.apply(s, new PlayerRef("test"), new Position("B1")));
    }
}
