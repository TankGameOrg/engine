package pro.trevor.tankgame.rule.definition;

import java.util.function.BiConsumer;
import pro.trevor.tankgame.rule.type.IMetaElement;
import pro.trevor.tankgame.state.State;

public class MetaTickActionRule<T extends IMetaElement> implements IApplicableRule<T> {

    private final BiConsumer<State, T> consumer;

    public MetaTickActionRule(BiConsumer<State, T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(state, subject);
    }
}
