package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;

public class LootDeadTankTest extends LootActionTestHelper {
    @Test
    void cantLootLivingTank() {
        setupTest("B2", 0, "B3", 0);
        Attribute.DEAD.to(targetTank, false);

        assertFalse(canApply(PlayerRules.LOOT_GOLD_FROM_DEAD_TANK, "B3"));
    }

    @Test
    void cantLootWall() {
        setupTest("B2", 0, "B3", 0);
        state.getBoard().putUnit(new BasicWall(new Position("A1"), 2));

        assertFalse(canApply(PlayerRules.LOOT_GOLD_FROM_DEAD_TANK, "A1"));
    }

    @Test
    void lootingTransfersGold() {
        setupTest("B2", 2, "B3", 3);
        apply(PlayerRules.LOOT_GOLD_FROM_DEAD_TANK, "B3");

        assertEquals(5, Attribute.GOLD.unsafeFrom(subjectTank));
        assertEquals(0, Attribute.GOLD.unsafeFrom(targetTank));
    }
}
