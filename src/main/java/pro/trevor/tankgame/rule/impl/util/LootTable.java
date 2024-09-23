package pro.trevor.tankgame.rule.impl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.attribute.AttributeContainer;
import pro.trevor.tankgame.util.Random;

public class LootTable implements ILootProvider {
    public static class Entry implements Comparable<Integer> {
        protected BiConsumer<State, AttributeContainer> grantLoot;
        protected int weight;
        protected int weightedIndex;

        /**
         * A type of loot that a player can recieve
         * @param weight the likelyhood that an entry will appear relative to the other entries in the table i.e. in a table with weights 1, 2, 3, the loot would appear 1/6, 2/6, 3/6 of the time
         * @param grantLoot a function that applies the loot to an attribute container (this could involve adding or removing attributes)
         */
        public Entry(int weight, BiConsumer<State, AttributeContainer> grantLoot) {
            this.grantLoot = grantLoot;
            this.weight = weight;
        }

        /**
         * Check if the given index is before, inside, or after our weight range i.e. weightedIndex (inclusive) to weightedIndex + weight (exclusive)
         */
        public int compareTo(Integer searchWeightedIndex) {
            if(searchWeightedIndex < weightedIndex) {
                return 1;
            }

            if(weightedIndex + weight <= searchWeightedIndex) {
                return -1;
            }

            return 0;
        }
    }

    private List<Entry> lootTable;
    private int weightedTotal; /// The sum of the weight of all of the entries in the table

    public LootTable(List<Entry> lootTable) {
        this();
        for(Entry entry : lootTable) {
            addLoot(entry);
        }
    }

    public LootTable() {
        weightedTotal = 0;
        lootTable = new ArrayList<>();
    }

    public void addLoot(Entry entry) {
        this.lootTable.add(entry);
        entry.weightedIndex = weightedTotal;
        weightedTotal += entry.weight;
    }

    public void grantLoot(State state, AttributeContainer target, AttributeContainer looter) {
        // No loot was applicable RIP you get nothing
        if(lootTable.isEmpty()) return;

        int weightedIndex = state.getOrElse(Attribute.RANDOM, new Random(0)).nextInt(weightedTotal);
        int index = Collections.binarySearch(lootTable, weightedIndex);
        Entry loot = lootTable.get(index);
        loot.grantLoot.accept(state, looter);
    }
}
