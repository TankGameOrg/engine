package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;

import pro.trevor.tankgame.state.attribute.Attribute;

public class DieRollLogFieldSpec<T> extends LogFieldSpec<DieRollResult> {
    private List<DiceSet<T>> dice;

    public DieRollLogFieldSpec(Attribute<DieRollResult> attribute, List<DiceSet<T>> dice) {
        super(attribute);
        this.dice = dice;
    }

    public List<DiceSet<T>> getDiceSets() {
        return dice;
    }
}
