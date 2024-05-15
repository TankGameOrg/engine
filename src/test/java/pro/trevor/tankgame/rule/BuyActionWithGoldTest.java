package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.util.DummyState;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.attribute.Attribute;

public class BuyActionWithGoldTest {

    @Test
    public void TypeCheckTest() {
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 1); // action_cost is a don't care
        assertEquals(PlayerRules.ActionKeys.BUY_ACTION, rule.name(),
                "Asserts that the buy action with gold rule is the right type");
    }

    @Test
    public void DeadTankCannotBuyAction() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, true).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 1);
        assertFalse(rule.canApply(new DummyState(), tank, 3)); // goldSpent = 3
    }

    @Test
    public void NoGoldCannotBuyAction() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 0)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 1);
        assertFalse(rule.canApply(new DummyState(), tank, 3)); // goldSpent = 3
    }

    @Test
    public void OneMaxBuyAttemptBuyTwo() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 6)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 1);
        assertFalse(rule.canApply(new DummyState(), tank, 6)); // goldSpent = 6
    }

    @Test
    public void ZeroGoldCostRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(0, 1));
    }

    @Test
    public void NegativeCostRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(-5, 1));
    }

    @Test
    public void ZeroMaxBuysRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(3, 0));
    }

    @Test
    public void NegativeMaxBuysRuleIllegal() {
        assertThrows(Error.class, () -> PlayerRules.BuyActionWithGold(3, -4));
    }

    @Test
    public void TooFewGoldCannotBuyAction() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 5)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 5);
        assertFalse(rule.canApply(new DummyState(), tank, 6)); // goldSpent = 6
    }

    @Test
    public void EnsureGoldSpentDivisibleByCost() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 5)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 5);
        assertFalse(rule.canApply(new DummyState(), tank, 4)); // goldSpent = 4
    }

    @Test
    public void BuyActionGainAction() {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, 3)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 1);
        rule.apply(new DummyState(), tank, 3); // goldSpent = 3

        assertEquals(0, tank.getGold());
        assertEquals(1, tank.getActions());
    }

    @ParameterizedTest()
    @CsvSource({
            "6,  1,  6,  3,  0",
            "16,  2,  9,  5,  7",
    })
    public void BuyActionsGainActions(int startingGold, int startingActions, int spentGold, int expectedActions,
            int expectedGold) {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, startingActions)
                .with(Attribute.GOLD, startingGold).with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3, 5);
        rule.apply(new DummyState(), tank, spentGold);

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
    public void CostMultiplesWork(int actionCost, int goldSpent, int expectedActions) {
        Tank tank = TankBuilder.buildV3Tank().with(Attribute.ACTION_POINTS, 0).with(Attribute.GOLD, goldSpent)
                .with(Attribute.DEAD, false).finish();

        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(actionCost, 5);
        rule.apply(new DummyState(), tank, goldSpent);

        assertEquals(0, tank.getGold());
        assertEquals(expectedActions, tank.getActions());
    }
}
