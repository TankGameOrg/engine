package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.UnwalkableFloor;
import pro.trevor.tankgame.state.board.floor.IFloor;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.LineOfSight;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class UnwalkableFloorTest {

    private PlayerRuleContext makeContext(State state, PlayerRef player, Position target, boolean hit) {
        return new ContextBuilder(state, player)
            .withTarget(target)
            .with(Attribute.HIT, hit)
            .finish();
    }

    @Test
    /* _ _ _
     * T X _
     */
    public void CannotWalkTest() {
        GenericTank t = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 1).at(new Position("A2")).finish();
        State s = TestUtilities.generateBoard(3, 2, t);
        s.getBoard().putFloor(new UnwalkableFloor(new Position("B2")));

        IPlayerRule moveRule = PlayerRules.getMoveRule(Attribute.ACTION_POINTS, 1);
        assertFalse(moveRule.canApply(makeContext(s, t.getPlayerRef(), new Position("B2"), true)).isEmpty());
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

        IPlayerRule shootRule = PlayerRules.SHOOT_V3;

        assertTrue(LineOfSight.hasLineOfSightV3(s, t.getPosition(), floor.getPosition()));

        shootRule.apply(makeContext(s, t.getPlayerRef(), floor.getPosition(), true));
        assertSame(s.getBoard().getFloor(floor.getPosition()).orElse(null), floor);

        shootRule.apply(makeContext(s, t.getPlayerRef(), floor.getPosition(), true));
        assertSame(s.getBoard().getFloor(floor.getPosition()).orElse(null), floor);

        shootRule.apply(makeContext(s, t.getPlayerRef(), floor.getPosition(), true));
        assertSame(s.getBoard().getFloor(floor.getPosition()).orElse(null), floor);
    }

}
