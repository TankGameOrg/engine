package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.Main;
import pro.trevor.tankgame.rule.definition.range.TypeRange;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.*;
import pro.trevor.tankgame.util.function.IVarTriConsumer;
import pro.trevor.tankgame.util.function.IVarTriPredicate;

import java.util.Arrays;

public class PlayerActionRule<T extends IPlayerElement> implements IPlayerRule<T> {

    protected final String name;
    protected final IVarTriPredicate<State, T, Object> predicate;
    protected final IVarTriConsumer<State, T, Object> consumer;
    protected final TypeRange<?>[] parameters;

    public PlayerActionRule(String name, IVarTriPredicate<State, T, Object> predicate,
            IVarTriConsumer<State, T, Object> consumer, TypeRange<?>... parameters) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
        this.parameters = parameters;
    }

    @Override
    public void apply(State state, T subject, Object... meta) {
        if (canApply(state, subject, meta)) {
            consumer.accept(state, subject, meta);
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);

            if (subject instanceof IJsonObject subjectJson) {
                error.put("subject", subjectJson.toJson());
            } else {
                error.put("subject", subject.getPlayerRef());
            }

            if (Main.DEBUG) {
                System.err.println(error.toString(2));
                System.err.println(state.toString());
            }
            throw new Error(
                    String.format("Failed to apply `%s` to `%s` given `%s`", name, subject, Arrays.toString(meta)));
        }
    }

    @Override
    public boolean canApply(State state, T subject) {
        // Player action rule requires meta data to actually check if it can be applied
        // so we assume it always can if we don't have any
        // TODO: Maybe this should be a separate interface?
        return true;
    }

    @Override
    public boolean canApply(State state, T subject, Object... meta) {
        return validateOptionalTypes(meta) && predicate.test(state, subject, meta);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TypeRange<?>[] parameters() {
        return parameters;
    }

    protected boolean validateOptionalTypes(Object[] meta) {
        if (meta.length != parameters.length) {
            return false;
        }
        for (int i = 0; i < parameters.length; ++i) {
            if (!parameters[i].getBoundClass().isAssignableFrom(meta[i].getClass())) {
                return false;
            }
        }
        return true;
    }
}
