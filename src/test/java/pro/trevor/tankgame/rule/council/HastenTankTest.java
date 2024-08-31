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

public class HastenTankTest {

    private static final int MODIFIER = 1;

    private static final PlayerConditionRule ZERO_COST_RULE = PlayerRules.getHastenRule(0, MODIFIER);
    private static final PlayerConditionRule ONE_COST_RULE = PlayerRules.getHastenRule(1, MODIFIER);

    @Test
    public void testCannotHaveNegativeCost() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getHastenRule(-1, MODIFIER));
    }

    @Test
    public void testCannotHaveZeroModifier() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getHastenRule(0, 0));
    }

    @Test
    public void testCannotHaveNegativeModifier() {
        Assertions.assertThrows(AssertionError.class, () -> PlayerRules.getHastenRule(0, -1));
    }

    PlayerRuleContext makeContext(State state, PlayerRef player, GenericTank target) {
        return new ContextBuilder(state, player)
            .withTarget(target)
            .finish();
    }

    boolean canApply(IPlayerRule rule, State state, PlayerRef player, GenericTank target) {
        return rule.canApply(makeContext(state, player, target)).isEmpty();
    }

    @Test
    public void testPlayerCannotHaveLivingTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, false).finish();
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.SPEED, 3).finish();
        State state = TestUtilities.generateBoard(2, 2, tank, otherTank);
        state.getPlayers().add(player);

        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), otherTank));
    }

    @Test
    public void testPlayerCanHaveDeadTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(1, 1)).with(Attribute.PLAYER_REF, player.toRef()).with(Attribute.DEAD, true).finish();
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.SPEED, 3).finish();
        State state = TestUtilities.generateBoard(2, 2, tank, otherTank);
        state.getPlayers().add(player);

        Assertions.assertTrue(canApply(ZERO_COST_RULE, state, player.toRef(), otherTank));
    }

    @Test
    public void testPlayerCanHaveNoTank() {
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank otherTank = TankBuilder.buildTank().at(new Position(0, 0)).with(Attribute.PLAYER_REF, new PlayerRef("other")).with(Attribute.DEAD, false).with(Attribute.SPEED, 3).finish();
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
                .with(Attribute.SPEED, 3)
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
                .with(Attribute.SPEED, 3)
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
                .with(Attribute.SPEED, 3)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        PlayerRules.getHastenRule(POWER_COST, 1).apply(makeContext(state, player.toRef(), tank));
        Assertions.assertEquals(POWER - POWER_COST, player.getUnsafe(Attribute.POWER));
    }

    @Test
    public void testCannotIfPlayerHasExistingSpeedModifier() {
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 1);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.SPEED, 3)
                .with(Attribute.PREVIOUS_SPEED, 2)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        Assertions.assertFalse(canApply(ZERO_COST_RULE, state, player.toRef(), tank));
    }

    @Test
    public void testReducesSpeedBySpecifiedAmount() {
        final int INITIAL_SPEED = 3;
        final int MODIFIER = 2;
        State state = new TestState();
        Player player = new Player("test");
        player.put(Attribute.POWER, 0);
        GenericTank tank = TankBuilder.buildTank().at(new Position(0, 0))
                .with(Attribute.NAME, "tank")
                .with(Attribute.SPEED, INITIAL_SPEED)
                .finish();
        state.getPlayers().add(player);
        state.getBoard().putUnit(tank);

        PlayerRules.getHastenRule(0, MODIFIER).apply(makeContext(state, player.toRef(), tank));
        Assertions.assertEquals(INITIAL_SPEED + MODIFIER, tank.getUnsafe(Attribute.SPEED));
    }
}
