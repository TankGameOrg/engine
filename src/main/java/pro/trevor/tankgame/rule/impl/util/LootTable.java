package pro.trevor.tankgame.rule.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.rule.impl.util.RandomManager;

public class LootTable {
    public static class Entry {
        protected BiConsumer<State, AttributeContainer> grantLoot;
        protected int weight;

        public Entry(int weight, BiConsumer<State, AttributeContainer> grantLoot) {
            this.grantLoot = grantLoot;
            this.weight = weight;
        }
    }

    private List<Entry> lootTable;

    public LootTable(List<Entry> lootTable) {
        this.lootTable = lootTable;
    }

    public LootTable() {
        this(new ArrayList<>());
    }

    public void addLoot(Entry entry) {
        this.lootTable.add(entry);
    }

    public void grantLoot(State state, AttributeContainer looter) {
        // No loot was applicable RIP you get nothing
        if(lootTable.isEmpty()) return;

        Entry loot = lootTable.get(RandomManager.randomizer.nextInt(lootTable.size()));
        loot.grantLoot.accept(state, looter);
    }
}
