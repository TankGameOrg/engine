package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.board.Board;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.IUnit;
import pro.trevor.tankgame.state.meta.Council;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pro.trevor.tankgame.rule.impl.shared.PlayerRules.SHOOT_V4;
import static pro.trevor.tankgame.util.TestUtilities.buildPositionedTank;

public class TankShootTest {

    private static State generateBoard(int width, int height, IUnit... units) {
        Board board = new Board(width, height);
        for (IUnit unit : units) {
            board.putUnit(unit);
        }
        return new State(board, new Council());
    }

    @Test
    void testDeadTankCannotShoot() {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, true);
        State state = generateBoard(2, 2, tank);
        assertThrows(Error.class, () -> SHOOT_V4.apply(state, tank, new Position("A2")));
    }

    @Test
    void testTankCannotShootWithoutActions() {
        Tank tank = buildPositionedTank("A1", 0, 0, 3, true);
        State state = generateBoard(2, 2, tank);
        assertThrows(Error.class, () -> SHOOT_V4.apply(state, tank, new Position("A2")));
    }

    @Test
    void testShootDamageWalls() {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, false);
        BasicWall wall = new BasicWall(new Position("A2"), 3);
        State state = generateBoard(2, 2, tank, wall);
        SHOOT_V4.apply(state, tank, new Position("A2"), true);
        assertEquals(2, wall.getDurability());
    }

    @Test
    void testShootDamageTanks() {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, false);
        Tank otherTank = buildPositionedTank("A2", 0, 0, 3, false);
        State state = generateBoard(2, 2, tank, otherTank);
        SHOOT_V4.apply(state, tank, new Position("A2"), true);
        assertEquals(2, otherTank.getDurability());
    }

    @Test
    void testShootMissDoesNotDamageTanks() {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, false);
        Tank otherTank = buildPositionedTank("A2", 0, 0, 3, false);
        State state = generateBoard(2, 2, tank, otherTank);
        SHOOT_V4.apply(state, tank, new Position("A2"), false);
        assertEquals(3, otherTank.getDurability());
    }

    @Test
    void testShootDamageDeadTank() {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, false);
        Tank otherTank = buildPositionedTank("A2", 0, 0, 3, true);
        State state = generateBoard(2, 2, tank, otherTank);
        SHOOT_V4.apply(state, tank, new Position("A2"), true);
        assertEquals(2, otherTank.getDurability());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0, 0",
        "1, 1, 0",
        "2, 1, 1",
        "3, 2, 1",
        "4, 3, 1",
        "5, 3, 2",
        "6, 4, 2",
        "7, 5, 2",
        "8, 6, 2",
        "9, 6, 3",
    })
    void testShootKillingLivingTankDistributesGold(int gold, int expectedNewGold, int expectedNewCoffer) {
        Tank tank = buildPositionedTank("A1", 1, 0, 3, false);
        Tank otherTank = buildPositionedTank("A2", 0, gold, 1, false);
        State state = generateBoard(2, 2, tank, otherTank);
        SHOOT_V4.apply(state, tank, new Position("A2"), true);
        assertEquals(expectedNewGold, tank.getGold());
        assertEquals(expectedNewCoffer, state.getCouncil().getCoffer());
    }

}
