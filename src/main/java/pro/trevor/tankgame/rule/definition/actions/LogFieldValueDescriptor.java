package pro.trevor.tankgame.rule.definition.actions;

import java.util.List;


public class LogFieldValueDescriptor<T> {
    private T value;
    private String prettyName;
    private List<LogFieldSpec<?>> nestedSpecs;

    public LogFieldValueDescriptor(T value) {
        this(value, value.toString());
    }

    public LogFieldValueDescriptor(T value, String prettyName) {
        this.value = value;
        this.prettyName = prettyName;
        this.nestedSpecs = List.of();
    }

    public LogFieldValueDescriptor(T value, List<LogFieldSpec<?>> nestedSpecs) {
        this(value);
        this.nestedSpecs = nestedSpecs;
    }

    public LogFieldValueDescriptor(T value, String prettyName, List<LogFieldSpec<?>> nestedSpecs) {
        this(value, prettyName);
        this.nestedSpecs = nestedSpecs;
    }

    public T getValue() {
        return value;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public List<LogFieldSpec<?>> getNestedSpecs() {
        return nestedSpecs;
    }
}
