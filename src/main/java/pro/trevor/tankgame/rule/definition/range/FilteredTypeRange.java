package pro.trevor.tankgame.rule.definition.range;

import java.util.HashSet;
import java.util.Set;

import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.function.ITriPredicate;

public class FilteredTypeRange<Subject, RangeType> extends FunctionVariableRange<Subject, RangeType> {

    private DiscreteTypeRange<RangeType> typeRange;

    public FilteredTypeRange(DiscreteTypeRange<RangeType> typeRange, ITriPredicate<State, Subject, RangeType> filterPredicate) {
        super(typeRange.getName(), (state, tank) -> getFiltered(state, tank, typeRange, filterPredicate));
        this.typeRange = typeRange;
    }

    private static <Subject, RangeType> Set<RangeType> getFiltered(State state, Subject subject, DiscreteTypeRange<RangeType> typeRange, ITriPredicate<State, Subject, RangeType> filterPredicate) {
        if (typeRange instanceof VariableTypeRange<?,?> variableRange) {
            VariableTypeRange<Object, ?> genericRange = (VariableTypeRange<Object, ?>) variableRange;
            genericRange.generate(state, subject);
        }

        Set<RangeType> output = new HashSet<>();
        for (RangeType element : typeRange.getElements()) {
            if(filterPredicate.test(state, subject, element)) {
                output.add(element);
            }
        }

        return output;
    }

    @Override
    public String getJsonDataType() {
        return typeRange.getJsonDataType();
    }

    @Override
    public Class<RangeType> getBoundClass() {
        return typeRange.getBoundClass();
    }
}
