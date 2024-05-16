package pro.trevor.tankgame.rule.definition.range;



import java.util.Set;

public interface DiscreteTypeRange<T> extends TypeRange<T> {

    Set<T> getElements();

}
