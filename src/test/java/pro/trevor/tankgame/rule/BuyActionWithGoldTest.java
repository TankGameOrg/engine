package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.util.TestUtilities;

public class BuyActionWithGoldTest {

    PlayerRuleContext makeContext(GenericTank tank, int gold) {
        State state = TestUtilities.generateBoard(1, 1, tank);

        return new ContextBuilder(state, tank.getPlayerRef())
            .with(Attribute.GOLD, gold)
            .finish();
    }

    boolean canApply(IPlayerRule rule, GenericTank tank, int gold) {
        return rule.canApply(makeContext(tank, gold)).isEmpty();
    }

    void apply(IPlayerRule rule, GenericTank tank, int gold) {
        rule.apply(makeContext(tank, gold));
    }

    @Test
    public void TypeCheckTest() {
        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 1); // action_cost is a don't care
        assertEquals(PlayerRules.ActionKeys.BUY_ACTION, rule.name(),
                "Asserts that the buy action with gold rule is the right type");
    }

    @Test
    public void DeadTankCannotBuyAction() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, true).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 1);
        assertFalse(canApply(rule, tank, 3)); // goldSpent = 3
    }

    @Test
    public void NoGoldCannotBuyAction() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 0)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 1);
        assertFalse(canApply(rule, tank, 3)); // goldSpent = 3
    }

    @Test
    public void OneMaxBuyAttemptBuyTwo() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 6)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 1);
        assertFalse(canApply(rule, tank, 6)); // goldSpent = 6
    }

    @Test
    public void ZeroGoldCostRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.buyActionWithGold(0, 1));
    }

    @Test
    public void NegativeCostRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.buyActionWithGold(-5, 1));
    }

    @Test
    public void ZeroMaxBuysRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.buyActionWithGold(3, 0));
    }

    @Test
    public void NegativeMaxBuysRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.buyActionWithGold(3, -4));
    }

    @Test
    public void TooFewGoldCannotBuyAction() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 5)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 5);
        assertFalse(canApply(rule, tank, 6));
    }

    @Test
    public void EnsureGoldSpentDivisibleByCost() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 5)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 5);
        assertFalse(canApply(rule, tank, 4));
    }

    @Test
    public void BuyActionGainAction() {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 1);
        apply(rule, tank, 3); // goldSpent = 3

        assertEquals(0, tank.getUnsafe(Attribute.GOLD));
        assertEquals(1, tank.getUnsafe(Attribute.ACTION_POINTS));
    }

    @ParameterizedTest()
    @CsvSource({
            "6,  1,  6,  3,  0",
            "16,  2,  9,  5,  7",
    })
    public void BuyActionsGainActions(int startingGold, int startingActions, int spentGold, int expectedActions,
            int expectedGold) {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, startingActions)
                .with(Attribute.GOLD, startingGold).with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(3, 5);
        apply(rule, tank, spentGold);

        assertEquals(expectedGold, tank.getUnsafe(Attribute.GOLD));
        assertEquals(expectedActions, tank.getUnsafe(Attribute.ACTION_POINTS));
    }

    @ParameterizedTest()
    @CsvSource({
            "3,   3,  1",
            "3,   6,  2",
            "3,   9,  3",
            "5,   5,  1",
            "5,  10,  2",
            "5,  25,  5"
    })
    public void CostMultiplesWork(int actionCost, int goldSpent, int expectedActions) {
        GenericTank tank = TankBuilder.buildTank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, goldSpent)
                .with(Attribute.DEAD, false).finish();

        IPlayerRule rule = PlayerRules.buyActionWithGold(actionCost, 5);
        apply(rule, tank, goldSpent);

        assertEquals(0, tank.getUnsafe(Attribute.GOLD));
        assertEquals(expectedActions, tank.getUnsafe(Attribute.ACTION_POINTS));
    }
}
