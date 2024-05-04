package pro.trevor.tankgame.Rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.TestUtil.TestUtilities;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;

public class BuyActionWithGoldTest 
{
    @Test
    public void TypeCheckTest()
    {
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3); // action_cost is a don't care
        assertEquals(PlayerRules.ActionKeys.BUY_ACTION, rule.name(), "Asserts that the buy action with gold rule is the right type");
    }

    @Test
    public void DeadTankCannotBuyAction()
    {
        Tank tank = TestUtilities.BuildTestTank(0, 3, true);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {3};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void NoGoldCannotBuyAction()
    {
        Tank tank = TestUtilities.BuildTestTank(0, 0, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {3};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void ZeroGoldCostRuleIllegal()
    {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(0));
    }

    @Test
    public void NegativeCostRuleIllegal()
    {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(-5));
    }

    @Test
    public void TooFewGoldCannotBuyAction()
    {
        Tank tank = TestUtilities.BuildTestTank(0, 5, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {6};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void EnsureGoldSpentDivisibleByCost()
    {
        Tank tank = TestUtilities.BuildTestTank(0, 5, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {4};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void BuyActionGainAction()
    {
        Tank tank = TestUtilities.BuildTestTank(0, 3, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {3};
        rule.apply(new State(1, 1), tank, meta);

        assertEquals(0, tank.getGold());
        assertEquals(1, tank.getActions());
    }

    @ParameterizedTest()
    @CsvSource({
        "6,  1,  6,  3,  0",
        "16,  2,  9,  5,  7",
    })
    public void BuyActionsGainActions(int startingGold, int startingActions, int spentGold, int expectedActions, int expectedGold)
    {
        Tank tank = TestUtilities.BuildTestTank(startingActions, startingGold, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        Integer[] meta = {spentGold};
        rule.apply(new State(1, 1), tank, meta);

        assertEquals(expectedGold, tank.getGold());
        assertEquals(expectedActions, tank.getActions());
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
    public void CostMultiplesWork(int actionCost, int goldSpent, int expectedActions)
    {
        Tank tank = TestUtilities.BuildTestTank(0, goldSpent, false);

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(actionCost);

        Integer[] meta = {goldSpent};
        rule.apply(new State(1, 1), tank, meta);

        assertEquals(0, tank.getGold());
        assertEquals(expectedActions, tank.getActions());
    }
}
