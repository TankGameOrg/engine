package pro.trevor.tankgame.rule.council;

import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.state.board.unit.GenericTank;

import static org.junit.jupiter.api.Assertions.*;

public class BountyTankTest {

    private static final IPlayerRule BASIC_BOUNTY_RULE = PlayerRules.getRuleCofferCostBounty(1, 1);
    private static final PlayerRef councilPlayer = new PlayerRef("Council");

    void applyBasicBounty(State state, GenericTank tank) {
        BASIC_BOUNTY_RULE.apply(
            new ContextBuilder(state, councilPlayer)
                .withTarget(tank)
                .with(Attribute.BOUNTY, 1)
                .finish()
        );
    }

    @Test
    public void testGrantBountyToLivingTank() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .with(Attribute.DEAD, false)
                .finish();
        State state = new TestState();
        state.getCouncil().put(Attribute.COFFER, 1);

        applyBasicBounty(state, tank);
        assertEquals(1, tank.getUnsafe(Attribute.BOUNTY));
    }

    @Test
    public void testGrantBountyToDeadTank() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .with(Attribute.DEAD, true)
                .finish();
        State state = new TestState();
        state.getCouncil().put(Attribute.COFFER, 1);

        assertThrows(Error.class, () -> applyBasicBounty(state, tank));
        assertEquals(0, tank.getUnsafe(Attribute.BOUNTY));
    }

    @Test
    public void testSubtractGoldFromCoffer() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, 1)
                .with(Attribute.BOUNTY, 0)
                .finish();
        State state = new TestState();
        state.getCouncil().put(Attribute.COFFER, 1);

        applyBasicBounty(state, tank);
        assertEquals(0, state.getCouncil().getUnsafe(Attribute.COFFER));
    }

    @Test
    public void testBountiesAreAdditive() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 3)
                .finish();
        State state = new TestState();
        state.getCouncil().put(Attribute.COFFER, 1);

        applyBasicBounty(state, tank);
        assertEquals(4, tank.getUnsafe(Attribute.BOUNTY));
    }

    @Test
    public void testErrorOnInsufficientGoldInCoffer() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .finish();
        State state = new TestState();

        assertThrows(Error.class, () -> applyBasicBounty(state, tank));
    }
}
