package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.rule.impl.shared.PlayerRules.GetRuleCofferCostGrantLife;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pro.trevor.tankgame.rule.definition.player.PlayerActionRule;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.meta.Council;
import pro.trevor.tankgame.util.DummyState;
import pro.trevor.tankgame.util.TestUtilities;

public class CouncilGrantLifeTest {

    private static final PlayerActionRule<Council> ZERO_COST_RULE = GetRuleCofferCostGrantLife(0);
    private static final PlayerActionRule<Council> ONE_COST_RULE = GetRuleCofferCostGrantLife(1);

    @Test
    public void testGrantLifeToLivingTank() {
        Tank tank = TestUtilities.buildDurableTestTank(0, 0, 1, false);
        State state = new DummyState();
        ZERO_COST_RULE.apply(state, state.getCouncil(), tank);
        assertEquals(2, tank.getDurability());
    }

    @ParameterizedTest
    @CsvSource({"1", "2", "3"})
    public void testGrantLifeToDeadTank(int durability) {
        Tank tank = TestUtilities.buildDurableTestTank(0, 0, durability, true);
        State state = new DummyState();
        state.getCouncil().getCouncillors().add(tank.getPlayer());
        ZERO_COST_RULE.apply(state, state.getCouncil(), tank);
        assertEquals(1, tank.getDurability());
        assertFalse(tank.isDead());
        assertFalse(state.getCouncil().getCouncillors().contains(tank.getPlayer()));
    }

    @Test
    public void testSubtractGoldFromCoffer() {
        Tank tank = TestUtilities.buildDurableTestTank(0, 0, 1, false);
        State state = new DummyState();
        state.getCouncil().setCoffer(1);
        ONE_COST_RULE.apply(state, state.getCouncil(), tank);
        assertEquals(0, state.getCouncil().getCoffer());
    }

    @Test
    public void testErrorOnInsufficientGoldInCoffer() {
        Tank tank = TestUtilities.buildDurableTestTank(0, 0, 1, false);
        State state = new DummyState();
        assertThrows(Error.class, () -> ONE_COST_RULE.apply(state, state.getCouncil(), tank));
    }
}
