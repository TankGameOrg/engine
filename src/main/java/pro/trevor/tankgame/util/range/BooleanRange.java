package pro.trevor.tankgame.util.range;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BooleanRange extends BaseDiscreteRange<Boolean> {

    private static final Set<Boolean> booleans = Stream.of(true, false).collect(Collectors.toSet());

    public BooleanRange(String name) {
        super(name, booleans);
    }

    @Override
    public String getDataType() {
        return "boolean";
    }
}
