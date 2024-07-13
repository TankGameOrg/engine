package pro.trevor.tankgame.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV3RulesetRegister;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.e2e.EndToEndTestUtils.*;

public class TestEndToEndV3 {

    private EndToEndTester tester;

    @BeforeEach
    public void initialize() {
        tester = new EndToEndTester(new DefaultV3RulesetRegister(), "src/test/resources/initial-v3.json", "src/test/resources/moves-v3.json");
    }

    @Test
    public void testStateAtEndState() {
        assertFalse(Attribute.RUNNING.unsafeFrom(tester.getState()));
        assertEquals("Corey", Attribute.WINNER.unsafeFrom(tester.getState()));
        assertEquals(18, Attribute.TICK.unsafeFrom(tester.getState()));
        assertEquals(15, tester.getCouncil().allPlayersOnCouncil().size());
        assertEquals(20, Attribute.COFFER.unsafeFrom(tester.getCouncil()));

        tester.getCouncil().getCouncillors().forEach((p) -> assertFalse(tester.getCouncil().isPlayerSenator(p)));
        tester.getCouncil().getSenators().forEach((p) -> assertFalse(tester.getCouncil().isPlayerCouncillor(p)));
    }

    @Test
    public void testPlayersAtEndState() {
        assertPlayerAttributeEquals(tester, "Corey", Attribute.GOLD, 69);
        assertPlayerAttributeEquals(tester, "Corey", Attribute.BOUNTY, 5);
        assertPlayerAttributeEquals(tester, "Corey", Attribute.ACTION_POINTS, 1);
        assertPlayerAttributeEquals(tester, "Corey", Attribute.DURABILITY, 3);
        assertPlayerAttributeEquals(tester, "Corey", Attribute.RANGE, 2);
        assertPlayerAttributeEquals(tester, "Corey", Attribute.POSITION, new Position("C6"));

        assertPlayerAttributeEquals(tester, "Beyer", Attribute.GOLD, 0);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.BOUNTY, 0);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.ACTION_POINTS, 0);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.DURABILITY, 3);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.RANGE, 2);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.DEAD, true);
        assertPlayerAttributeEquals(tester, "Beyer", Attribute.POSITION, new Position("C5"));

        assertPlayerAttributeEquals(tester, "Stomp", Attribute.RANGE, 3);
    }

    @Test
    public void testCouncillorsAndSenatorsAtEndState() {
        assertExpectedCouncillorsAndSenators(tester,
                Set.of("Beyer", "Bryan", "David", "Isaac", "Joel", "John", "Lena", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"),
                Set.of("Dan", "Ryan", "Steve"));
    }

    @Test
    public void testPlayersOnBoardAtEndState() {
        assertExpectedTanksOnBoard(tester,
                Set.of("Corey"),
                Set.of("Beyer", "Bryan", "David", "Isaac", "Joel", "John", "Lena", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"),
                Set.of("Dan", "Ryan", "Steve"));
    }

}
