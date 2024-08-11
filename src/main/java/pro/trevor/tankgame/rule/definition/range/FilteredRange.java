package pro.trevor.tankgame.rule.definition.range;

import java.util.HashSet;
import java.util.Set;

import jakarta.json.JsonValue.ValueType;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.function.ITriPredicate;

public class FilteredRange<SubjectType, ValueType> extends FunctionVariableRange<SubjectType, ValueType> {
    private DiscreteTypeRange<ValueType> sourceRange;

    public FilteredRange(DiscreteTypeRange<ValueType> sourceRange, ITriPredicate<State, SubjectType, ValueType> filter) {
        super(sourceRange.getName(), (state, subject) -> generateFilteredRange(sourceRange, filter, state, subject));
        this.sourceRange = sourceRange;
    }

    private static <SubjectType, ValueType> Set<ValueType> generateFilteredRange(DiscreteTypeRange<ValueType> range, ITriPredicate<State, SubjectType, ValueType> filter, State state, SubjectType subject) {
        if (range instanceof VariableTypeRange<?, ?> variableRange) {
            ((VariableTypeRange<SubjectType, ValueType>) variableRange).generate(state, subject);
        }

        HashSet<ValueType> filteredRange = new HashSet<>();
        for(ValueType value : range.getElements()) {
            if(filter.test(state, subject, value)) {
                filteredRange.add(value);
            }
        }

        return filteredRange;
    }

    public Class<ValueType> getBoundClass() {
        return sourceRange.getBoundClass();
    }

    public String getJsonDataType() {
        return sourceRange.getJsonDataType();
    }
}
