package pro.trevor.tankgame.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV4RulesetRegister;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.trevor.tankgame.e2e.EndToEndTestUtils.*;

public class TestEndToEndV4 {

    private EndToEndTester tester;

    @BeforeEach
    public void initialize() {
        tester = new EndToEndTester(new DefaultV4RulesetRegister(), "src/test/resources/initial-v4.json", "src/test/resources/moves-v4.json");
    }

    @Test
    public void testStateAtEndState() {
        EndToEndTestUtils.testState(tester, false, "John", 10);
    }

    @Test
    public void testCouncilAtEndState() {
        EndToEndTestUtils.testCouncil(tester, 14, 1);
        assertEquals(0, tester.getCouncil().getUnsafe(Attribute.COFFER));
        assertEquals(34, tester.getCouncil().getUnsafe(Attribute.ARMISTICE));
    }

    @Test
    public void testPlayersAtEndState() {
        assertPlayerTankAttributeEquals(tester, "John", Attribute.GOLD, 1);
        assertPlayerTankAttributeEquals(tester, "John", Attribute.BOUNTY, 0);
        assertPlayerTankAttributeEquals(tester, "John", Attribute.ACTION_POINTS, 1);
        assertPlayerTankAttributeEquals(tester, "John", Attribute.DURABILITY, 1);
        assertPlayerTankAttributeEquals(tester, "John", Attribute.RANGE, 3);
        assertPlayerTankAttributeEquals(tester, "John", Attribute.POSITION, new Position("J10"));
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
                Set.of("Beyer", "Bryan", "Charlie", "Corey", "Dan", "David", "Isaac", "Lena", "Ryan", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"));
    }

    @Test
    public void testGoldMinesOnBoardAtEndState() {
        assertTypeOfFloorAtPosition(tester, new Position("D9"), WalkableFloor.class);
        assertTypeOfFloorAtPosition(tester, new Position("D3"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("D4"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C4"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C9"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C10"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("D10"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("D11"), GoldMine.class);
    }

    @Test
    public void testWallsOnBoardAtEndState() {
        assertTypeOfUnitAtPosition(tester, new Position("B4"), BasicWall.class);
        assertTypeOfUnitAtPosition(tester, new Position("K4"), BasicWall.class);
        assertTypeOfUnitAtPosition(tester, new Position("I12"), BasicWall.class);
        assertTypeOfUnitAtPosition(tester, new Position("J12"), BasicWall.class);
        assertEquals(1, ((BasicWall) tester.getUnitAtPosition(new Position("B4"))).getUnsafe(Attribute.DURABILITY));
        assertEquals(2, ((BasicWall) tester.getUnitAtPosition(new Position("K4"))).getUnsafe(Attribute.DURABILITY));
        assertEquals(4, ((BasicWall) tester.getUnitAtPosition(new Position("I12"))).getUnsafe(Attribute.DURABILITY));
        assertEquals(6, ((BasicWall) tester.getUnitAtPosition(new Position("J12"))).getUnsafe(Attribute.DURABILITY));
    }

}
