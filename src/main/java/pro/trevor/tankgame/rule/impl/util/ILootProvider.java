package pro.trevor.tankgame.rule.impl.util;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.state.attribute.AttributeContainer;

public interface ILootProvider {
    /**
     * Give looter some loot (and possibly remove it from target) based on the state/target that they looted
     * @param state The current state where the loot should be awarded
     * @param looter The attribute object performing the loot action
     * @param target The attribute object being looted
     */
    void grantLoot(State state, AttributeContainer target, AttributeContainer looter);
}
