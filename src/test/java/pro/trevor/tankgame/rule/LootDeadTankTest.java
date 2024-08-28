package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.util.LootTable;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;

public class LootDeadTankTest extends LootActionTestHelper {
    private PlayerConditionRule makeLootRule() {
        return PlayerRules.getLootRule(new LootTable());
    }

    @Test
    void playerCanOnlyLootOncePerDay() {
        setupTest("A1", 0);
        addTargetTank("B1", 0);

        // First loot attempt works
        apply(makeLootRule(), "B1");

        // We've already looted once
        assertFalse(canApply(makeLootRule(), "B1"));

        startNewDay();

        // It's a new day so we can loot again
        assertTrue(canApply(makeLootRule(), "B1"));
    }

    @Test
    void cantLootLivingTank() {
        setupTest("B2", 0);
        addTargetTank("B3", 0);
        targetTank.put(Attribute.DEAD, false);

        assertFalse(canApply(makeLootRule(), "B3"));
    }

    @Test
    void cantLootWall() {
        setupTest("B2", 0);
        addTargetTank("B3", 0);
        state.getBoard().putUnit(new BasicWall(new Position("A1"), 2));

        assertFalse(canApply(makeLootRule(), "A1"));
    }

    @Test
    void lootingTransfersGold() {
        setupTest("B2", 2);
        addTargetTank("B3", 3);
        apply(makeLootRule(), "B3");

        assertEquals(5, subjectTank.getUnsafe(Attribute.GOLD));
        assertEquals(0, targetTank.getUnsafe(Attribute.GOLD));
    }
}
