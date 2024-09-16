package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.state.board.unit.Tank;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.util.ContextBuilder;
import pro.trevor.tankgame.util.TankBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static pro.trevor.tankgame.rule.impl.shared.PlayerRules.SHOOT_V5;
import static pro.trevor.tankgame.util.TestUtilities.generateBoard;

public class TankShootV5Test {

    private PlayerRuleContext makeContext(State state, PlayerRef plyaer, Position position, boolean hit) {
        return new ContextBuilder(state, plyaer)
            .withTarget(position)
            .with(Attribute.HIT, hit)
            .finish();
    }

    private Tank buildTank(String position, int gold, int bounty, String playerName) {
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
    public void testShootKillingLivingTankWithBountyAndGold() {
        Tank tank = buildTank("A1", 0, 0, "player1");
        Tank targetTank = buildTank("A2", 1, 5, "player2");

        State state = generateBoard(2, 2, tank, targetTank);
        SHOOT_V5.apply(makeContext(state, tank.getPlayerRef(), new Position("A2"), true));

        assertEquals(0, tank.getUnsafe(Attribute.GOLD));
        assertEquals(6, targetTank.getUnsafe(Attribute.GOLD));
        assertEquals(tank.getPlayerRef(), targetTank.getUnsafe(Attribute.ONLY_LOOTABLE_BY));
        assertEquals(0, state.getCouncil().getUnsafe(Attribute.COFFER));
    }

    @Test
    public void testShootKillingLivingTankKeepsGold() {
        Tank tank = buildTank("B2", 1, 0, "player2");
        Tank targetTank = buildTank("A1", 4, 0, "player1");

        State state = generateBoard(2, 2, tank, targetTank);
        SHOOT_V5.apply(makeContext(state, tank.getPlayerRef(), new Position("A1"), true));

        assertEquals(1, tank.getUnsafe(Attribute.GOLD));
        assertEquals(4, targetTank.getUnsafe(Attribute.GOLD));
        assertEquals(tank.getPlayerRef(), targetTank.getUnsafe(Attribute.ONLY_LOOTABLE_BY));
        assertEquals(0, state.getCouncil().getUnsafe(Attribute.COFFER));
    }
}
