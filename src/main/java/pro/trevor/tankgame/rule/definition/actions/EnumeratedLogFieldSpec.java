package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;

import pro.trevor.tankgame.state.attribute.Attribute;


public class EnumeratedLogFieldSpec<T> extends LogFieldSpec<T> {
    private List<LogFieldValueDescriptor<T>> descriptors;

    public EnumeratedLogFieldSpec(Attribute<T> attribute, List<LogFieldValueDescriptor<T>> descriptors) {
        super(attribute);
        this.descriptors = descriptors;
    }

    /**
     * Return a list of acceptable parameters for this field
     */
    public List<LogFieldValueDescriptor<T>> getValueDescriptors() {
        return descriptors;
    }
}
