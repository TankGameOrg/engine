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
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.util.TestUtilities;

public class SmiteTankTest {

    private static final int HEALTH = 1;

    private static final PlayerConditionRule ZERO_COST_RULE = PlayerRules.getSmiteRule(0, HEALTH);
    private static final PlayerConditionRule ONE_COST_RULE = PlayerRules.getSmiteRule(1, HEALTH);

    private PlayerRuleContext makeContext(State state, PlayerRef playerRef, GenericTank tank) {
        return new ContextBuilder(state, playerRef)
            .withTarget(tank)
            .finish();
    }

    private boolean canApply(IPlayerRule rule, State state, PlayerRef playerRef, GenericTank tank) {
        return rule.canApply(makeContext(state, playerRef, tank)).isEmpty();
    }

    @Test
    public void testCannotHaveNegativeCost() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSmiteRule(-1, HEALTH));
    }

    @Test
    public void testCannotHaveZeroHealth() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSmiteRule(0, 0));
    }

    @Test
    public void testCannotHaveNegativeHealth() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getSmiteRule(0, -1));
    }

    @Test
    public void testPlayerCannotHaveLivingTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, false).finish();
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.DURABILITY, 3).finish();
        State state = TestUtilities.generateBoard(2, 2, tank, otherTank);
        state.getPlayers().add(player);

        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), otherTank));
    }

    @Test
    public void testPlayerCanHaveDeadTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, true).finish();
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.DURABILITY, 3).finish();
        State state = TestUtilities.generateBoard(2, 2, tank, otherTank);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), otherTank));
    }

    @Test
    public void testPlayerCanHaveNoTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.DURABILITY, 3).finish();
        State state = TestUtilities.generateBoard(1, 1, otherTank);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), otherTank));
    }

    @Test
    public void testCannotUseWithoutSufficientPower() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.DURABILITY, 10)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        Assertions.assertFalse(canApply(ONE_COST_RULE, state, player.toRef(), tank));
    }

    @Test
    public void testCanUseWithSufficientPower() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 1);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.DURABILITY, 10)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        Assertions.assertTrue(canApply(ONE_COST_RULE, state, player.toRef(), tank));
    }

    @Test
    public void testSubtractsFromPower() {
        final int POWER =  5;
        final int POWER_COST = 2;
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, POWER);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.DURABILITY, 10)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        PlayerRules.getSmiteRule(POWER_COST, 1).apply(makeContext(state, player.toRef(), tank));
        Assertions.assertEquals(POWER - POWER_COST, player.getUnsafe(Attribute.POWER));
    }

    @Test
    public void testSmiteDoesSpecifiedDamage() {
        final int INITIAL_DURABILITY = 10;
        final int HEALTH = 2;
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.DURABILITY, INITIAL_DURABILITY)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        PlayerRules.getSmiteRule(0, HEALTH).apply(makeContext(state, player.toRef(), tank));
        Assertions.assertEquals(INITIAL_DURABILITY - HEALTH, tank.getUnsafe(Attribute.DURABILITY));
    }

}
