package pro.trevor.tankgame.util.range;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class DiscreteIntegerRange extends BaseDiscreteRange<Integer> {

    public DiscreteIntegerRange(String name, Set<Integer> elements) {
        super(name, elements);
    }

    // lower and upper are both inclusive; range : [lower, upper]
    public DiscreteIntegerRange(String name, int lower, int upper) {
        super(name, new HashSet<>(IntStream.rangeClosed(lower, upper).boxed().toList()));
        assert lower <= upper;
    }
}
