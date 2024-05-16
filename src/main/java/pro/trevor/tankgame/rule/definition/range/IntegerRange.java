package pro.trevor.tankgame.rule.definition.range;

public class IntegerRange extends BaseRange<Integer> {

    public IntegerRange(String name) {
        super(name);
    }

    @Override
    public String getJsonDataType() {
        return "integer";
    }

    @Override
    public Class<Integer> getBoundClass() {
        return Integer.class;
    }
}
