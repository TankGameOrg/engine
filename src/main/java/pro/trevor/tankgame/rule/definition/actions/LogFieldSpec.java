package pro.trevor.tankgame.rule.definition.actions;

import pro.trevor.tankgame.state.attribute.Attribute;

public abstract class LogFieldSpec<T> {
    private Attribute<T> attribute;
    private String description;

    public LogFieldSpec(Attribute<T> attribute) {
        this.attribute = attribute;
        this.description = "";
    }

    /**
     * Get the attribute used to identify this log field
     */
    public Attribute<T> getAttribute() {
        return attribute;
    }

    /**
     * Get a description of this field
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of this field
     * @return this log field spec
     */
    public LogFieldSpec<T> setDescription(String description) {
        this.description = description;
        return this;
    }
}
