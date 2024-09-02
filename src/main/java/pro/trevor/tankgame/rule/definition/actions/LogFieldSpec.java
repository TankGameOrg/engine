package pro.trevor.tankgame.rule.definition.actions;

import pro.trevor.tankgame.state.attribute.Attribute;

public abstract class LogFieldSpec<T> {
    private Attribute<T> attribute;

    public LogFieldSpec(Attribute<T> attribute) {
        this.attribute = attribute;
    }

    /**
     * Get the attribute used to identify this log field
     */
    public Attribute<T> getAttribute() {
        return attribute;
    }
}
