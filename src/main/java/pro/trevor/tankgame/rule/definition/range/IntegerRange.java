package pro.trevor.tankgame.rule.definition.range;

public class IntegerRange extends BaseRange<Integer> {

    public IntegerRange(String name) {
        super(name);
    }

    @Override
    public String getDataType() {
        return "integer";
    }
}
