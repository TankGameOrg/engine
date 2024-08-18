package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;

public class LootDeadTankTest extends LootActionTestHelper {
    @Test
    void cantLootPositionsOutsideBoard() {
        setupTest("B2", 0, "B3", 0);
        assertFalse(canApply(PlayerRules.LOOT_DEAD_TANK, "J22"));
    }

    @Test
    void cantLootLivingTank() {
        setupTest("B2", 0, "B3", 0);
        Attribute.DEAD.to(targetTank, false);

        assertFalse(canApply(PlayerRules.LOOT_DEAD_TANK, "B3"));
    }

    @Test
    void cantLootWall() {
        setupTest("B2", 0, "B3", 0);
        state.getBoard().putUnit(new BasicWall(new Position("A1"), 2));

        assertFalse(canApply(PlayerRules.LOOT_DEAD_TANK, "A1"));
    }

    @Test
    void cantLootProtectedTankTillNextDay() {
        setupTest("A1", 0, "B1", 5);
        Attribute.ONLY_LOOTABLE_BY.to(targetTank, new PlayerRef("Pam"));
        state.getPlayers().add(new Player("Pam"));

        assertFalse(canApply(PlayerRules.LOOT_DEAD_TANK, "B1"));
        startNewDay();
        assertTrue(canApply(PlayerRules.LOOT_DEAD_TANK, "B1"));
    }

    @Test
    void cantLootTankProtectedForUs() {
        setupTest("A1", 0, "B1", 5);
        Attribute.ONLY_LOOTABLE_BY.to(targetTank, subjectTank.getPlayerRef());

        assertTrue(canApply(PlayerRules.LOOT_DEAD_TANK, "B1"));
    }
}
