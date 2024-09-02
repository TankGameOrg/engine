package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleContext;
import pro.trevor.tankgame.rule.definition.player.PlayerRuleError;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.BasicWall;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;


public class LootingTargetTest extends LootActionTestHelper {
    private static class GoldLootTransfer implements ITriConsumer<PlayerRuleContext, GenericTank, AttributeContainer> {
        public void accept(PlayerRuleContext context, GenericTank tank, AttributeContainer target) {
            // Just set the gold to the other tank's value this method just exists to verify that we called the consumer
            tank.put(Attribute.GOLD, target.getUnsafe(Attribute.GOLD));
            target.put(Attribute.GOLD, 0);
        };
    }

    PlayerConditionRule getBasicLootRule() {
        return PlayerRules.getLootTargetRule((context, tank, target) -> {
            return target.getUnsafe(Attribute.POSITION).equals(new Position("B3")) ?
                Optional.of(new PlayerRuleError(PlayerRuleError.Category.GENERIC, "No")) :
                Optional.empty();
        }, new GoldLootTransfer());
    }

    @Test
    void playerCanOnlyLootTargetsInTheirRange() {
        setupTest("D1", 0);
        addTargetTank("A3", 0);

        // Subject has a range of 2 so A3 is out of reach (distance 3)
        assertFalse(canApply(getBasicLootRule(), "A3"));
    }

    @Test
    void deadTanksCantLoot() {
        setupTest("B2", 0);
        addTargetTank("A3", 0);
        subjectTank.put(Attribute.DEAD, true);

        assertFalse(canApply(getBasicLootRule(), "A3"));
    }

    @Test
    void cantLootTargetsIfPredicateRejectsThem() {
        setupTest("B2", 0);
        addTargetTank("B3", 0);
        assertFalse(canApply(getBasicLootRule(), "B3"));
    }

    @Test
    void lootingTriggersTheTransferGoldConsumer() {
        setupTest("A1", 0);
        addTargetTank("B1", 5);
        apply(getBasicLootRule(), "B1");

        assertEquals(5, subjectTank.getUnsafe(Attribute.GOLD));
        assertEquals(0, targetTank.getUnsafe(Attribute.GOLD));
    }

    @Test
    void cantLootPositionsOutsideBoard() {
        setupTest("B2", 0);
        addTargetTank("B3", 0);
        assertFalse(canApply(getBasicLootRule(), "J22"));
    }

    @Test
    void cantLootProtectedTankTillNextDay() {
        setupTest("A1", 0);
        addTargetTank("B1", 5);
        targetTank.put(Attribute.ONLY_LOOTABLE_BY, new PlayerRef("Pam"));
        state.getPlayers().add(new Player("Pam"));

        assertFalse(canApply(getBasicLootRule(), "B1"));
        startNewDay();
        assertTrue(canApply(getBasicLootRule(), "B1"));
    }

    @Test
    void cantLootTankProtectedForUs() {
        setupTest("A1", 0);
        addTargetTank("B1", 5);
        targetTank.put(Attribute.ONLY_LOOTABLE_BY, subjectTank.getPlayerRef());

        assertTrue(canApply(getBasicLootRule(), "B1"));
    }

    @Test
    void cantLootWithoutLineOfSight() {
        setupTest("A1", 0);
        state.getBoard().putUnit(new BasicWall(new Position("A2"), 1));
        addTargetTank("A3", 0);

        assertFalse(canApply(getBasicLootRule(), "A3"));
    }
}
