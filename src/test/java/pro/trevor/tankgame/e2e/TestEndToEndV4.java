package pro.trevor.tankgame.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV4RulesetRegister;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pro.trevor.tankgame.e2e.EndToEndTestUtils.*;

public class TestEndToEndV4 {

    private EndToEndTester tester;

    @BeforeEach
    public void initialize() {
        tester = new EndToEndTester(new DefaultV4RulesetRegister(), "src/test/resources/initial-v4.json", "src/test/resources/moves-v4.json");
    }

    @Test
    public void testStateAtEndState() {
        assertFalse(Attribute.RUNNING.unsafeFrom(tester.getState()));
        assertEquals("John", Attribute.WINNER.unsafeFrom(tester.getState()));
        assertEquals(10, Attribute.TICK.unsafeFrom(tester.getState()));
        assertEquals(15, tester.getCouncil().allPlayersOnCouncil().size());
        assertEquals(0, Attribute.COFFER.unsafeFrom(tester.getCouncil()));

        tester.getCouncil().getCouncillors().forEach((p) -> assertFalse(tester.getCouncil().isPlayerSenator(p)));
        tester.getCouncil().getSenators().forEach((p) -> assertFalse(tester.getCouncil().isPlayerCouncillor(p)));
    }

    @Test
    public void testPlayersAtEndState() {
        assertPlayerAttributeEquals(tester, "John", Attribute.GOLD, 1);
        assertPlayerAttributeEquals(tester, "John", Attribute.BOUNTY, 0);
        assertPlayerAttributeEquals(tester, "John", Attribute.ACTION_POINTS, 1);
        assertPlayerAttributeEquals(tester, "John", Attribute.DURABILITY, 1);
        assertPlayerAttributeEquals(tester, "John", Attribute.RANGE, 3);
        assertPlayerAttributeEquals(tester, "John", Attribute.POSITION, new Position("J10"));
    }

    @Test
    public void testCouncillorsAndSenatorsAtEndState() {
        assertExpectedCouncillorsAndSenators(tester,
                Set.of("Beyer", "Bryan", "Charlie", "Corey", "Dan", "David", "Isaac", "Lena", "Ryan", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"),
                Set.of("Mike"));
    }

    @Test
    public void testPlayersOnBoardAtEndState() {
        assertExpectedTanksOnBoard(tester,
                Set.of("John"),
                Set.of("Beyer", "Bryan", "Charlie", "Corey", "Dan", "David", "Isaac", "Lena", "Ryan", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"),
                Set.of("Mike"));
    }

}
