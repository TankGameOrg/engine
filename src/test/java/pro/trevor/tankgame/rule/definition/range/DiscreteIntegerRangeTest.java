package pro.trevor.tankgame.rule.definition.range;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import pro.trevor.tankgame.rule.definition.range.DiscreteIntegerRange;

public class DiscreteIntegerRangeTest {
    @Test
    public void CanGenerateIntegersForContinuousRange() {
        DiscreteIntegerRange range = new DiscreteIntegerRange("continuous", 2, 7);
        JSONObject jsonRange = range.toJson();
        List<Integer> rangeArray = (List<Integer>) (Object) jsonRange.getJSONArray("range").toList();
        assertEquals(List.of(2, 3, 4, 5, 6, 7), rangeArray);
    }
}
