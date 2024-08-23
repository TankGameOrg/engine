package pro.trevor.tankgame.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.impl.ruleset.DefaultV3RulesetRegister;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.floor.GoldMine;
import pro.trevor.tankgame.state.board.floor.WalkableFloor;
import pro.trevor.tankgame.state.board.unit.BasicWall;

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
        EndToEndTestUtils.testState(tester, false, "Corey", 18);
        assertFalse(tester.getState().getUnsafe(Attribute.RUNNING));
        assertEquals("Corey", tester.getState().getUnsafe(Attribute.WINNER));
        assertEquals(18, tester.getState().getUnsafe(Attribute.TICK));
    }

    @Test
    public void testCouncilAtEndState() {
        EndToEndTestUtils.testCouncil(tester, 12, 3);
        assertEquals(20, tester.getCouncil().getUnsafe(Attribute.COFFER));
    }

    @Test
    public void testPlayersAtEndState() {
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.GOLD, 69);
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.BOUNTY, 5);
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.ACTION_POINTS, 1);
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.DURABILITY, 3);
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.RANGE, 2);
        assertPlayerTankAttributeEquals(tester, "Corey", Attribute.POSITION, new Position("C6"));

        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.GOLD, 0);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.BOUNTY, 0);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.ACTION_POINTS, 0);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.DURABILITY, 3);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.RANGE, 2);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.DEAD, true);
        assertPlayerTankAttributeEquals(tester, "Beyer", Attribute.POSITION, new Position("C5"));

        assertPlayerTankAttributeEquals(tester, "Stomp", Attribute.RANGE, 3);
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
                Set.of("Beyer", "Bryan", "David", "Isaac", "Joel", "John", "Lena", "Schmude", "Stomp", "Trevor", "Ty", "Xavion"));
    }

    @Test
    public void testGoldMinesOnBoardAtEndState() {
        assertTypeOfFloorAtPosition(tester, new Position("B4"), WalkableFloor.class);
        assertTypeOfFloorAtPosition(tester, new Position("C4"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C5"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C6"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C7"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("C8"), GoldMine.class);
        assertTypeOfFloorAtPosition(tester, new Position("D8"), GoldMine.class);
    }

    @Test
    public void testWallsOnBoardAtEndState() {
        assertTypeOfUnitAtPosition(tester, new Position("D4"), BasicWall.class);
        assertEquals(3, ((BasicWall) tester.getUnitAtPosition(new Position("D4"))).getUnsafe(Attribute.DURABILITY));
    }

}
