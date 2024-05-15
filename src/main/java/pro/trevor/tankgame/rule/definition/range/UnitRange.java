package pro.trevor.tankgame.rule.definition.range;

import pro.trevor.tankgame.state.attribute.Attribute;
import pro.trevor.tankgame.state.board.unit.GenericTank;
import pro.trevor.tankgame.state.meta.Council;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UnitRange<S, T> extends FunctionVariableRange<S, T> {

    public static UnitRange<Council, GenericTank> ALL_TANKS = new UnitRange<>("target", GenericTank.class, (t) -> true);
    public static UnitRange<Council, GenericTank> ALL_DEAD_TANKS = new UnitRange<>("target", GenericTank.class, (t) -> Attribute.DEAD.from(t).orElse(false));
    public static UnitRange<Council, GenericTank> ALL_LIVING_TANKS = new UnitRange<>("target", GenericTank.class, (t) -> !Attribute.DEAD.from(t).orElse(false));

    private UnitRange(String name, Class<T> unitClass, Predicate<? super T> filter) {
        super(name, (state, subject) -> state.getBoard().gatherUnits(unitClass).stream().filter(filter).collect(Collectors.toSet()));
    }

    @Override
    public String getDataType() {
        return "tank";
    }
}
