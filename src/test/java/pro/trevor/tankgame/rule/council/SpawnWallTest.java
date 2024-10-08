package pro.trevor.tankgame.rule.council;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.UnwalkableFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.util.TestUtilities;

public class SpawnWallTest {

    private static final int DURABILITY = 1;

    private static final PlayerConditionRule ZERO_COST_RULE = PlayerRules.getSpawnWallWithCostRule(0, DURABILITY);
    private static final PlayerConditionRule ONE_COST_RULE = PlayerRules.getSpawnWallWithCostRule(1, DURABILITY);

    private PlayerRuleContext makeContext(State state, PlayerRef playerRef, Position target) {
        return new ContextBuilder(state, playerRef)
            .withTarget(target)
            .finish();
    }

    private boolean canApply(IPlayerRule rule, State state, PlayerRef playerRef, Position target) {
        return rule.canApply(makeContext(state, playerRef, target)).isEmpty();
    }


    @Test
    public void testCannotHaveNegativeCost() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSpawnWallWithCostRule(-1, DURABILITY));
    }

    @Test
    public void testCannotHaveZeroDurability() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSpawnWallWithCostRule(0, 0));
    }

    @Test
    public void testCannotHaveNegativeDurability() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSpawnWallWithCostRule(0, -1));
    }

    @Test
    public void testPlayerCannotHaveLivingTank() {
        Player player = new Player("test");
        Tank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, false).finish();
        State state = TestUtilities.generateBoard(2, 2, tank);
        state.getPlayers().add(player);

        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testPlayerCanHaveDeadTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        Tank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, true).finish();
        State state = TestUtilities.generateBoard(2, 2, tank);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testPlayerCanHaveNoTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        State state = TestUtilities.generateBoard(1, 1);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testCannotPlaceOnOccupiedUnit() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        state.getPlayers().add(player);

        state.getBoard().putUnit(new BasicWall(new Position(0, 0), 1));
        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testCannotPlaceOnOccupiedFloor() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        state.getPlayers().add(player);

        state.getBoard().putFloor(new UnwalkableFloor(new Position(0, 0)));
        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testCannotUseWithoutSufficientPower() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        state.getPlayers().add(player);

        Assertions.assertFalse(canApply(ONE_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testCanUseWithNoCostOnEmptyPosition() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testCanUseWithSufficientPower() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 1);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ONE_COST_RULE, state, player.toRef(), new Position(0, 0)));
    }

    @Test
    public void testSubtractsFromPower() {
        final int POWER =  5;
        final int POWER_COST = 2;
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, POWER);
        state.getPlayers().add(player);

        PlayerRules.getSpawnWallWithCostRule(POWER_COST, 1).apply(makeContext(state, player.toRef(), new Position(0, 0)));
        Assertions.assertEquals(POWER - POWER_COST, player.getUnsafe(Attribute.POWER));
    }

    @Test
    public void testWallHasSpecifiedDurability() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        state.getPlayers().add(player);

        ZERO_COST_RULE.apply(makeContext(state, player.toRef(), new Position(0, 0)));
        Object wall = state.getBoard().getUnit(new Position(0, 0)).get();
        Assertions.assertInstanceOf(BasicWall.class, wall);
        Assertions.assertEquals(DURABILITY, ((BasicWall) wall).getDurability());
    }
}
