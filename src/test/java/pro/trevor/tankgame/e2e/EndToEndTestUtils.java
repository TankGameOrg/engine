package pro.trevor.tankgame.e2e;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EndToEndTestUtils {

    public static String readFile(String path) {
        File file = new File(path);
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new Error("Failed to read file " +  path, e);
        }
    }

    public static void assertExpectedCouncillorsAndSenators(EndToEndTester tester, Set<String> councillors, Set<String> senators) {
        Set<String> actualCouncillors = tester.getCouncil().getCouncillors().stream().map(PlayerRef::getName).collect(Collectors.toSet());
        Set<String> actualSenators = tester.getCouncil().getSenators().stream().map(PlayerRef::getName).collect(Collectors.toSet());

        assertEquals(actualCouncillors, councillors);
        assertEquals(actualSenators, senators);
    }

    public static void assertExpectedTanksOnBoard(EndToEndTester tester, Set<String> livingTanks, Set<String> deadTanks, Set<String> noTanks) {
        Set<String> actualLivingTanks = tester.getBoard().gatherUnits(GenericTank.class).stream()
                .filter((t) -> !Attribute.DEAD.unsafeFrom(t))
                .map((t) -> t.getPlayerRef().getName())
                .collect(Collectors.toSet());
        Set<String> actualDeadTanks = tester.getBoard().gatherUnits(GenericTank.class).stream()
                .filter(Attribute.DEAD::unsafeFrom)
                .map((t) -> t.getPlayerRef().getName())
                .collect(Collectors.toSet());

        assertEquals(actualLivingTanks, livingTanks);
        assertEquals(actualDeadTanks, deadTanks);

        noTanks.forEach((p) -> assertFalse(tester.getBoard().gatherUnits(GenericTank.class)
                .stream().map((t) -> t.getPlayerRef().getName())
                .anyMatch(p::equals)));
    }

    public static <T> void assertPlayerTankAttributeEquals(EndToEndTester tester, String player, Attribute<T> attribute, T value) {
        assertEquals(value, attribute.unsafeFrom(tester.getTankByPlayerName(player)));
    }

    public static void testCouncil(EndToEndTester tester, int councillors, int senators) {
        assertEquals(councillors, tester.getCouncil().getCouncillors().size());
        assertEquals(senators, tester.getCouncil().getSenators().size());

        tester.getCouncil().getCouncillors().forEach((p) -> assertFalse(tester.getCouncil().isPlayerSenator(p)));
        tester.getCouncil().getSenators().forEach((p) -> assertFalse(tester.getCouncil().isPlayerCouncillor(p)));
    }

    public static void testState(EndToEndTester tester, boolean running, String winner, int tick) {
        assertEquals(running, Attribute.RUNNING.unsafeFrom(tester.getState()));
        assertEquals(winner, Attribute.WINNER.unsafeFrom(tester.getState()));
        assertEquals(tick, Attribute.TICK.unsafeFrom(tester.getState()));
    }

    public static <T> void assertTypeOfFloorAtPosition(EndToEndTester tester, Position position, Class<T> type) {
        assertEquals(type, tester.getFloorAtPosition(position).getClass());
    }

    public static <T> void assertTypeOfUnitAtPosition(EndToEndTester tester, Position position, Class<T> type) {
        assertEquals(type, tester.getUnitAtPosition(position).getClass());
    }

}
