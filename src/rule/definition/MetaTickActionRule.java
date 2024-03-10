package rule.definition;

import rule.type.IMetaTickElement;
import state.State;

import java.util.function.BiConsumer;

public class MetaTickActionRule<T extends IMetaTickElement> implements IApplicableRule<T> {

    private final BiConsumer<T, State> consumer;

    public MetaTickActionRule(BiConsumer<T, State> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void apply(State state, T subject) {
        consumer.accept(subject, state);
    }

}
