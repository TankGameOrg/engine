package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.UnwalkableFloor;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestUtilities;

public class UnwalkableFloorTest {

    @Test
    /* _ _ _
     * T X _
     */
    public void CannotWalkTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 1).at(new Position("A2")).finish();
        State s = TestUtilities.generateBoard(3, 2, t);
        s.getBoard().putFloor(new UnwalkableFloor(new Position("B2")));

        PlayerActionRule<GenericTank> moveRule = PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1);
        assertFalse(moveRule.canApply(s, t, new Position("B2")));
    }

    @Test
    /* _ _ _
     * T X T
     */
    public void CanShootAcross() {
        GenericTank t = TankBuilder.buildTank().at(new Position("A2")).finish();
        GenericTank t2 = TankBuilder.buildTank().at(new Position("C2")).finish();
        State s = TestUtilities.generateBoard(3, 2, t, t2);
        s.getBoard().putFloor(new UnwalkableFloor(new Position("B2")));

        assertTrue(LineOfSight.hasLineOfSightV3(s, t.getPosition(), t2.getPosition()));
        assertTrue(LineOfSight.hasLineOfSightV4(s, t.getPosition(), t2.getPosition()));
    }

    @Test
    /* _ _ _
     * T X _
     */
    public void CannotDestroy() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 3).with(Attribute.DEAD, false).with(Attribute.RANGE, 2).at(new Position("A2")).finish();
        State s = TestUtilities.generateBoard(3, 2, t);
        IFloor floor = new UnwalkableFloor(new Position("B2"));
        s.getBoard().putFloor(floor);

        PlayerActionRule<GenericTank> shootRule = PlayerRules.SHOOT_V3;

        assertTrue(LineOfSight.hasLineOfSightV3(s, t.getPosition(), floor.getPosition()));

        shootRule.apply(s, t, floor.getPosition(), true);
        assertTrue(s.getBoard().getFloor(floor.getPosition()).orElse(null) == floor);

        shootRule.apply(s, t, floor.getPosition(), true);
        assertTrue(s.getBoard().getFloor(floor.getPosition()).orElse(null) == floor);

        shootRule.apply(s, t, floor.getPosition(), true);
        assertTrue(s.getBoard().getFloor(floor.getPosition()).orElse(null) == floor);
    }

}
