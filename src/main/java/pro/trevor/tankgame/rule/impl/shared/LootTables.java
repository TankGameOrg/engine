package pro.trevor.tankgame.rule.impl.shared;

import java.util.List;
import java.util.function.BiConsumer;

import pro.trevor.tankgame.rule.impl.util.LootTable;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public class LootTables {
    private static BiConsumer<State, AttributeContainer> AddToAttribute(Attribute<Integer> attribute, int amountToAdd) {
        return (state, looter) -> {
            looter.put(attribute, looter.getOrElse(attribute, 0) + amountToAdd);
        };
    }

    private static <T> BiConsumer<State, AttributeContainer> SetAttribute(Attribute<T> attribute, T value) {
        return (state, looter) -> {
            looter.put(attribute, value);
        };
    }

    private static final int COMMON_WEIGHT = 4;
    private static final int UNCOMMON_WEIGHT = 2;
    private static final int RARE_WEIGHT = 1;

    public static final LootTable V5_LOOT_BOX_LOOT = new LootTable(
        List.of(
            new LootTable.Entry(COMMON_WEIGHT, LootTables.AddToAttribute(Attribute.ACTION_POINTS, 1)),
            new LootTable.Entry(COMMON_WEIGHT, LootTables.AddToAttribute(Attribute.GOLD, 2)),
            new LootTable.Entry(UNCOMMON_WEIGHT, LootTables.AddToAttribute(Attribute.DURABILITY, 2)),
            new LootTable.Entry(UNCOMMON_WEIGHT, LootTables.AddToAttribute(Attribute.GOLD, 5)),
            new LootTable.Entry(RARE_WEIGHT, LootTables.AddToAttribute(Attribute.MAX_ACTION_POINTS, 1)),
            new LootTable.Entry(RARE_WEIGHT, LootTables.AddToAttribute(Attribute.MAX_DURABILITY, 1))
        )
    );
}
