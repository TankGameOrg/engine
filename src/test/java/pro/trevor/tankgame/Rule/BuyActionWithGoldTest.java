package pro.trevor.tankgame.Rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.Tank;

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
        // Tank with 0 actions and 3 gold, but is dead
        Tank tank = new Tank("test", new Position(0, 0), 0, 3, 0, 2, 0, true);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule
        Integer[] meta = {3};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void NoGoldCannotBuyAction()
    {
        // Tank with 0 actions and 0 gold
        Tank tank = new Tank("test", new Position(0, 0), 0, 0, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule
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
        // Tank with 0 actions and 5 gold, but tries to spend 6
        Tank tank = new Tank("test", new Position(0, 0), 0, 5, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule
        Integer[] meta = {6};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void NonCostMultipleFails()
    {
        // Tank with 0 actions and 5 gold
        Tank tank = new Tank("test", new Position(0, 0), 0, 5, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule, but tries to spend 4, a non-multiple of the cost
        Integer[] meta = {4};
        assertFalse(rule.canApply(new State(1, 1), tank, meta));
    }

    @Test
    public void BuyActionGainAction()
    {
        // Tank with 0 actions and 3 gold
        Tank tank = new Tank("test", new Position(0, 0), 0, 3, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule
        Integer[] meta = {3};
        rule.apply(new State(1, 1), tank, meta);

        // have 0 gold and 1 action
        assertEquals(0, tank.getGold());
        assertEquals(1, tank.getActions());
    }

    @ParameterizedTest()
	@CsvSource({
           "6,  1,  6,  3,  0",
           "16,  2,  9,  5,  7",
	})
    public void BuyActionsGainActions(int starting_gold, int starting_actions, int spent_gold, int expected_actions, int expected_gold)
    {
        Tank tank = new Tank("test", new Position(0, 0), starting_actions, starting_gold, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(3);

        // apply rule
        Integer[] meta = {spent_gold};
        rule.apply(new State(1, 1), tank, meta);

        assertEquals(expected_gold, tank.getGold());
        assertEquals(expected_actions, tank.getActions());
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
    public void CostMultiplesWork(int action_cost, int gold_spent, int expected_actions)
    {
        // Tank with 0 actions and 3 gold
        Tank tank = new Tank("test", new Position(0, 0), 0, gold_spent, 3, 2, 0, false);

        // Get 3 gold-cost rule
        PlayerActionRule<Tank> rule = PlayerRules.BuyActionWithGold(action_cost);

        // apply rule
        Integer[] meta = {gold_spent};
        rule.apply(new State(1, 1), tank, meta);

        // have 0 gold and expected actions
        assertEquals(0, tank.getGold());
        assertEquals(expected_actions, tank.getActions());
    }
}
