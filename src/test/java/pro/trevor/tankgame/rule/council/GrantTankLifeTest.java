package pro.trevor.tankgame.rule.council;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.util.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.rule.impl.shared.PlayerRules.getRuleCofferCostGrantLife;

public class GrantTankLifeTest {

    private static final IPlayerRule ZERO_COST_RULE = getRuleCofferCostGrantLife(0, 0);
    private static final IPlayerRule ONE_COST_RULE = getRuleCofferCostGrantLife(1, 0);
    private static final PlayerRef councilPlayer = new PlayerRef("Council");

    private void apply(IPlayerRule rule, State state, Tank tank) {
        rule.apply(
            new ContextBuilder(state, councilPlayer)
                .withTarget(tank)
                .finish()
        );
    }

    @Test
    public void testGrantLifeToLivingTank() {
        Tank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false)
                .finish();
        State state = TestUtilities.generateBoard(1, 1, tank);
        state.getCouncil().getCouncillors().add(councilPlayer);
        apply(ZERO_COST_RULE, state, tank);
        assertEquals(2, tank.getUnsafe(Attribute.DURABILITY));
    }

    @ParameterizedTest
    @CsvSource({
            "1", "2", "3"
    })
    public void testGrantLifeToDeadTank(int durability) {
        Tank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, durability)
                .with(Attribute.DEAD, true)
                .finish();
        State state = TestUtilities.generateBoard(1, 1, tank);
        state.getCouncil().getCouncillors().add(tank.getPlayerRef());
        state.getCouncil().getCouncillors().add(councilPlayer);

        apply(ZERO_COST_RULE, state, tank);

        assertEquals(1, tank.getUnsafe(Attribute.DURABILITY));
        assertFalse(tank.getUnsafe(Attribute.DEAD));
        assertFalse(state.getCouncil().getCouncillors().contains(tank.getPlayerRef()));
    }

    @Test
    public void testSubtractGoldFromCoffer() {
        Tank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false)
                .finish();
        State state = TestUtilities.generateBoard(1, 1, tank);
        state.getCouncil().put(Attribute.COFFER, 1);
        state.getCouncil().getCouncillors().add(councilPlayer);
        apply(ONE_COST_RULE, state, tank);
        assertEquals(0, state.getCouncil().getUnsafe(Attribute.COFFER));
    }

    @Test
    public void testErrorOnInsufficientGoldInCoffer() {
        Tank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, 1)
                .with(Attribute.DEAD, false)
                .finish();
        State state = TestUtilities.generateBoard(1, 1, tank);
        state.getCouncil().getCouncillors().add(councilPlayer);
        assertThrows(Error.class, () -> apply(ONE_COST_RULE, state, tank));
    }

}
