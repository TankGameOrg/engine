package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.TankBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.rule.impl.shared.PlayerRules.SHOOT_V5;
import static pro.trevor.tankgame.util.TestUtilities.generateBoard;

public class TankShootV5Test {

    private GenericTank buildTank(String position, int gold, int bounty, String playerName) {
        return TankBuilder.buildTank()
            .at(new Position(position))
            .with(Attribute.PLAYER_REF, new PlayerRef(playerName))
            .with(Attribute.ACTION_POINTS, 1)
            .with(Attribute.DURABILITY, 1)
            .with(Attribute.RANGE, 2)
            .with(Attribute.BOUNTY, bounty)
            .with(Attribute.GOLD, gold)
            .finish();
    }

    @Test
    void testShootKillingLivingTankWithBountyAndGold() {
        GenericTank tank = buildTank("A1", 0, 0, "player1");
        GenericTank targetTank = buildTank("A2", 1, 5, "player2");

        State state = generateBoard(2, 2, tank, targetTank);
        SHOOT_V5.apply(state, tank.getPlayerRef(), new Position("A2"), true);

        assertEquals(0, Attribute.GOLD.unsafeFrom(tank));
        assertEquals(6, Attribute.GOLD.unsafeFrom(targetTank));
        assertEquals(tank.getPlayerRef(), Attribute.ONLY_LOOTABLE_BY.unsafeFrom(targetTank));
        assertEquals(0, Attribute.COFFER.unsafeFrom(state.getCouncil()));
    }

    @Test
    void testShootKillingLivingTankKeepsGold() {
        GenericTank tank = buildTank("B2", 1, 0, "player2");
        GenericTank targetTank = buildTank("A1", 4, 0, "player1");

        State state = generateBoard(2, 2, tank, targetTank);
        SHOOT_V5.apply(state, tank.getPlayerRef(), new Position("A1"), true);

        assertEquals(1, Attribute.GOLD.unsafeFrom(tank));
        assertEquals(4, Attribute.GOLD.unsafeFrom(targetTank));
        assertEquals(tank.getPlayerRef(), Attribute.ONLY_LOOTABLE_BY.unsafeFrom(targetTank));
        assertEquals(0, Attribute.COFFER.unsafeFrom(state.getCouncil()));
    }
}
