package pro.trevor.tankgame.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeObject;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Player;
import pro.trevor.tankgame.state.meta.PlayerRef;
import pro.trevor.tankgame.util.Result;
import pro.trevor.tankgame.util.function.ITriConsumer;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;


class GoldLootTransfer implements ITriConsumer<State, GenericTank, AttributeObject> {
    public void accept(State state, GenericTank tank, AttributeObject target) {
        // Just set the gold to the other tank's value this method just exists to verify that we called the consumer
        Attribute.GOLD.to(tank, Attribute.GOLD.unsafeFrom(target));
        Attribute.GOLD.to(target, 0);
    };
}


public class LootingTargetTest extends LootActionTestHelper {
    PlayerConditionRule getBasicLootRule() {
        return PlayerRules.getLootTargetRule((state, tank, target) -> {
            return Attribute.POSITION.unsafeFrom(target).equals(new Position("B3")) ?
                Result.error("No") :
                Result.ok();
        }, new GoldLootTransfer());
    }

    @Test
    void playerCanOnlyLootOncePerDay() {
        setupTest("A1", 0, "B1", 0);

        // First loot attempt works
        apply(getBasicLootRule(), "B1");

        // We've already looted once
        assertFalse(canApply(getBasicLootRule(), "B1"));

        startNewDay();

        // It's a new day so we can loot again
        assertTrue(canApply(getBasicLootRule(), "B1"));
    }

    @Test
    void playerCanOnlyLootTargetsInTheirRange() {
        setupTest("C1", 0, "A3", 0);

        // Subject has a range of 1 so A3 is out of reach (distance 2)
        assertFalse(canApply(getBasicLootRule(), "A3"));
    }

    @Test
    void deadTanksCantLoot() {
        setupTest("B2", 0, "A3", 0);
        Attribute.DEAD.to(subjectTank, true);

        // Subject has a range of 1 so A3 is out of reach (distance 2)
        assertFalse(canApply(getBasicLootRule(), "A3"));
    }

    @Test
    void cantLootTargetsIfPredicateRejectsThem() {
        setupTest("B2", 0, "B3", 0);
        assertFalse(canApply(getBasicLootRule(), "B3"));
    }

    @Test
    void lootingTriggersTheTransferGoldConsumer() {
        setupTest("A1", 0, "B1", 5);
        apply(getBasicLootRule(), "B1");

        assertEquals(5, Attribute.GOLD.unsafeFrom(subjectTank));
        assertEquals(0, Attribute.GOLD.unsafeFrom(targetTank));
    }

    @Test
    void cantLootPositionsOutsideBoard() {
        setupTest("B2", 0, "B3", 0);
        assertFalse(canApply(getBasicLootRule(), "J22"));
    }

    @Test
    void cantLootProtectedTankTillNextDay() {
        setupTest("A1", 0, "B1", 5);
        Attribute.ONLY_LOOTABLE_BY.to(targetTank, new PlayerRef("Pam"));
        state.getPlayers().add(new Player("Pam"));

        assertFalse(canApply(getBasicLootRule(), "B1"));
        startNewDay();
        assertTrue(canApply(getBasicLootRule(), "B1"));
    }

    @Test
    void cantLootTankProtectedForUs() {
        setupTest("A1", 0, "B1", 5);
        Attribute.ONLY_LOOTABLE_BY.to(targetTank, subjectTank.getPlayerRef());

        assertTrue(canApply(getBasicLootRule(), "B1"));
    }
}
