package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.definition.player.IPlayerRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.TankBuilder;
import pro.trevor.tankgame.util.TestState;
import pro.trevor.tankgame.state.board.unit.GenericTank;

import static org.junit.jupiter.api.Assertions.*;

public class CouncilBountyTest {

    private static final IPlayerRule BASIC_BOUNTY_RULE = PlayerRules.getRuleCofferCostBounty(1, 1);
    private static final PlayerRef councilPlayer = new PlayerRef("Council");

    @Test
    public void testGrantBountyToLivingTank() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .with(Attribute.DEAD, false)
                .finish();
        State state = new TestState();
        Attribute.COFFER.to(state.getCouncil(), 1);

        BASIC_BOUNTY_RULE.apply(state, councilPlayer, tank, 1);
        assertEquals(1, Attribute.BOUNTY.unsafeFrom(tank));
    }

    @Test
    public void testGrantBountyToDeadTank() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .with(Attribute.DEAD, true)
                .finish();
        State state = new TestState();
        Attribute.COFFER.to(state.getCouncil(), 1);

        assertThrows(Error.class, () -> BASIC_BOUNTY_RULE.apply(state, councilPlayer, tank, 1));
        assertEquals(0, Attribute.BOUNTY.unsafeFrom(tank));
    }

    @Test
    public void testSubtractGoldFromCoffer() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.DURABILITY, 1)
                .with(Attribute.BOUNTY, 0)
                .finish();
        State state = new TestState();
        Attribute.COFFER.to(state.getCouncil(), 1);

        BASIC_BOUNTY_RULE.apply(state, councilPlayer, tank, 1);
        assertEquals(0, Attribute.COFFER.unsafeFrom(state.getCouncil()));
    }

    @Test
    public void testBountiesAreAdditive() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 3)
                .finish();
        State state = new TestState();
        Attribute.COFFER.to(state.getCouncil(), 1);

        BASIC_BOUNTY_RULE.apply(state, councilPlayer, tank, 1);
        assertEquals(4, Attribute.BOUNTY.unsafeFrom(tank));
    }

    @Test
    public void testErrorOnInsufficientGoldInCoffer() {
        GenericTank tank = TankBuilder.buildTank()
                .with(Attribute.BOUNTY, 0)
                .finish();
        State state = new TestState();

        assertThrows(Error.class, () -> BASIC_BOUNTY_RULE.apply(state, councilPlayer, tank, 1));
    }
}
