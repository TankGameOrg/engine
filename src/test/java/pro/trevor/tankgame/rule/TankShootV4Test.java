package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pro.trevor.tankgame.rule.impl.version3.Tank;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attributes;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.util.DummyState;
import pro.trevor.tankgame.util.TankBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.rule.impl.shared.rule.PlayerRules.SHOOT_V4;
import static pro.trevor.tankgame.util.TestUtilities.generateBoard;

public class TankShootV4Test {

        @Test
        void testDeadTankCannotShoot() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, true)
                                .finish();
                State state = generateBoard(2, 2, tank);

                assertFalse(SHOOT_V4.canApply(state, tank, new Position("A2")));
        }

        @Test
        void testTankCannotShootWithoutActions() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 0)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank);

                assertFalse(SHOOT_V4.canApply(state, tank, new Position("A2")));
        }

        @Test
        void testShootDecrementsActions() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();

                SHOOT_V4.apply(new DummyState(), tank, new Position("A1"), false);

                assertEquals(0, tank.getActions());
        }

        @Test
        void testShootEmpty() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank);

                // Has no side effects, this test only ensures that it does not error
                SHOOT_V4.apply(state, tank, new Position("A2"), true);
        }

        @Test
        void testShootDamageWalls() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                BasicWall wall = new BasicWall(new Position("A2"), 3);
                State state = generateBoard(2, 2, tank, wall);

                SHOOT_V4.apply(state, tank, new Position("A2"), true);

                assertEquals(2, wall.getDurability());
        }

        @Test
        void testShootDamageTanks() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);

                SHOOT_V4.apply(state, tank, new Position("A2"), true);

                assertEquals(2, otherTank.getDurability());
        }

        @Test
        void testShootMissDoesNotDamageTanks() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);

                SHOOT_V4.apply(state, tank, new Position("A2"), false);

                assertEquals(3, otherTank.getDurability());
        }

        @Test
        void testShootDamageDeadTank() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.DEAD, true)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);

                SHOOT_V4.apply(state, tank, new Position("A2"), true);

                assertEquals(2, otherTank.getDurability());
        }

        @Test
        void testShootDamageSelf() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(1, 1, tank);

                SHOOT_V4.apply(state, tank, new Position("A1"), true);

                assertEquals(2, tank.getDurability());
        }

        @Test
        void testShootOutOfBoundsThrows() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(1, 1, tank);

                assertFalse(SHOOT_V4.canApply(state, tank, new Position("A2"), true));
        }

        @ParameterizedTest
        @CsvSource({
                        "0, 0, 0",
                        "1, 1, 0",
                        "2, 1, 1",
                        "3, 2, 1",
                        "4, 3, 1",
                        "5, 4, 1",
                        "6, 4, 2",
                        "7, 5, 2",
                        "8, 6, 2",
                        "9, 7, 2",
        })
        void testShootKillingLivingTankDistributesGold(int gold, int expectedNewGold, int expectedNewCoffer) {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.GOLD, 0)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 1)
                                .with(Attributes.GOLD, gold)
                                .with(Attributes.BOUNTY, 0)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);

                SHOOT_V4.apply(state, tank, new Position("A2"), true);

                assertEquals(expectedNewGold, tank.getGold());
                assertEquals(expectedNewCoffer, state.getCouncil().getCoffer());
        }

        @Test
        void testShootKillingLivingTankDistributesBounty() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.GOLD, 0)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 1)
                                .with(Attributes.GOLD, 0)
                                .with(Attributes.BOUNTY, 5)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);

                SHOOT_V4.apply(state, tank, new Position("A2"), true);

                assertEquals(5, tank.getGold());
                assertEquals(0, state.getCouncil().getCoffer());
        }

        @Test
        void testShootKillingLivingTankDistributesBountyAndGold() {
                Tank tank = TankBuilder.buildV3Tank()
                                .at(new Position("A1"))
                                .with(Attributes.ACTION_POINTS, 1)
                                .with(Attributes.DURABILITY, 3)
                                .with(Attributes.RANGE, 2)
                                .with(Attributes.GOLD, 0)
                                .with(Attributes.DEAD, false)
                                .finish();
                Tank otherTank = TankBuilder.buildV3Tank()
                                .at(new Position("A2"))
                                .with(Attributes.DURABILITY, 1)
                                .with(Attributes.GOLD, 1)
                                .with(Attributes.BOUNTY, 5)
                                .with(Attributes.DEAD, false)
                                .finish();
                State state = generateBoard(2, 2, tank, otherTank);
                SHOOT_V4.apply(state, tank, new Position("A2"), true);
                assertEquals(6, tank.getGold());
                assertEquals(0, state.getCouncil().getCoffer());
        }

}
