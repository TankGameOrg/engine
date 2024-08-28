package pro.trevor.tankgame.rule;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import pro.trevor.tankgame.rule.definition.player.PlayerConditionRule;
import pro.trevor.tankgame.rule.impl.shared.ConditionalRules;
import pro.trevor.tankgame.rule.impl.shared.PlayerRules;
import pro.trevor.tankgame.rule.impl.util.LootTable;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.Position;
import pro.trevor.tankgame.state.board.unit.EmptyUnit;
import pro.trevor.tankgame.state.board.unit.LootBox;


public class LootLootBoxTest extends LootActionTestHelper {
    LootBox lootBox;

    void addLootBox(String location) {
        lootBox = new LootBox(new Position(location));
        state.getBoard().putUnit(lootBox);
    }

    PlayerConditionRule makeBasicLootRule() {
        return PlayerRules.getLootRule(new LootTable(List.of(
            new LootTable.Entry(1, (state, looter) -> looter.put(Attribute.GOLD, 5))
        )));
    }

    @Override
    protected void startNewDay() {
        super.startNewDay();
        ConditionalRules.DESTORY_EMPTY_LOOT_BOXES.apply(state, lootBox);
    }

    @Test
    void testLootingGrantsLoot() {
        setupTest("A1", 0);
        addLootBox("B2");

        apply(makeBasicLootRule(), "B2");

        assertEquals(5, subjectTank.getUnsafe(Attribute.GOLD));

        startNewDay();

        assertEquals(EmptyUnit.class, state.getBoard().getUnit(new Position("B2")).get().getClass());
    }
}
