package pro.trevor.tankgame.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV3RulesetRegister;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

import static org.junit.jupiter.api.Assertions.*;

public class TestEndToEndV3 {

    private EndToEndTester tester;

    @BeforeEach
    public void initialize() {
        tester = new EndToEndTester(new DefaultV3RulesetRegister(), "src/test/resources/initial-v3.json", "src/test/resources/moves-v3.json");
    }

    @Test
    public void testEndGameState() {
        assertFalse(Attribute.RUNNING.unsafeFrom(tester.getState()));
        assertEquals("Corey", Attribute.WINNER.unsafeFrom(tester.getState()));
        assertEquals(18, Attribute.TICK.unsafeFrom(tester.getState()));
        assertEquals(15, tester.getCouncil().allPlayersOnCouncil().size());
        assertEquals(20, Attribute.COFFER.unsafeFrom(tester.getCouncil()));
        assertTrue(tester.getBoard().gatherUnits(GenericTank.class).stream().anyMatch((t) -> t.getPlayerRef().getName().equals("Corey")));
        assertTrue(tester.getBoard().gatherUnits(GenericTank.class).stream().anyMatch((t) -> t.getPlayerRef().getName().equals("Beyer")));
        assertTrue(tester.getBoard().gatherUnits(GenericTank.class).stream().noneMatch((t) -> t.getPlayerRef().getName().equals("Dan")));
        assertTrue(tester.getBoard().gatherUnits(GenericTank.class).stream().noneMatch((t) -> t.getPlayerRef().getName().equals("Ryan")));
        assertTrue(tester.getBoard().gatherUnits(GenericTank.class).stream().noneMatch((t) -> t.getPlayerRef().getName().equals("Steve")));
        assertTrue(tester.getCouncil().isPlayerSenator(new PlayerRef("Dan")));
        assertFalse(tester.getCouncil().isPlayerCouncillor(new PlayerRef("Dan")));
        assertFalse(tester.getCouncil().isPlayerSenator(new PlayerRef("Trevor")));
        assertTrue(tester.getCouncil().isPlayerCouncillor(new PlayerRef("Trevor")));
        assertEquals(69, Attribute.GOLD.unsafeFrom(tester.getTankByPlayerName("Corey")));
        assertEquals(5, Attribute.BOUNTY.unsafeFrom(tester.getTankByPlayerName("Corey")));
        assertEquals(1, Attribute.ACTION_POINTS.unsafeFrom(tester.getTankByPlayerName("Corey")));
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(tester.getTankByPlayerName("Corey")));
        assertEquals(2, Attribute.RANGE.unsafeFrom(tester.getTankByPlayerName("Corey")));
        assertEquals(new Position("C6"), tester.getTankByPlayerName("Corey").getPosition());
        assertEquals(0, Attribute.GOLD.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(0, Attribute.BOUNTY.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(0, Attribute.ACTION_POINTS.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(3, Attribute.DURABILITY.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(2, Attribute.RANGE.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(new Position("C5"), tester.getTankByPlayerName("Beyer").getPosition());
        assertTrue(Attribute.DEAD.unsafeFrom(tester.getTankByPlayerName("Beyer")));
        assertEquals(3, Attribute.RANGE.unsafeFrom(tester.getTankByPlayerName("Stomp")));
    }

}
