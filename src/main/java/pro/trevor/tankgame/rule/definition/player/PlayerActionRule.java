package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.IJsonObject;
import pro.trevor.tankgame.util.IVarQuadConsumer;
import pro.trevor.tankgame.util.IVarQuadPredicate;

public class PlayerActionRule<T extends IPlayerElement, U> implements IPlayerRule<T, U> {

    private final String name;
    private final IVarQuadPredicate<State, T, U, Object> predicate;
    private final IVarQuadConsumer<State, T, U, Object> consumer;
    private final Class<?>[] optional;

    public PlayerActionRule(String name, IVarQuadPredicate<State, T, U, Object> predicate, IVarQuadConsumer<State, T, U, Object> consumer, Class<?>... optional) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
        this.optional = optional;
    }

    @Override
    public void apply(State state, T subject, U target, Object... meta) {
        if (canApply(state, subject, target, meta)) {
            consumer.accept(state, subject, target, meta);
        } else {
            JSONObject error = new JSONObject();
            error.put("error", true);
            error.put("rule", name);

            if (subject instanceof IJsonObject subjectJson) {
                error.put("subject", subjectJson.toJson());
            } else {
                error.put("subject", subject.getPlayer());
            }

            if (target instanceof IJsonObject targetJson) {
                error.put("target", targetJson.toJson());
            } else {
                error.put("target", target.toString());
            }

            System.out.println(error.toString(2));
            throw new Error(String.format("Failed to apply `%s` to `%s` and `%s`", name, subject, target));
        }
    }

    @Override
    public boolean canApply(State state, T subject, U target, Object... meta) {
        return validateOptionalTypes(meta) && predicate.test(state, subject, target, meta);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?>[] optional() {
        return optional;
    }

    private boolean validateOptionalTypes(Object[] meta) {
        if (meta.length != optional.length) {
            return false;
        }
        for (int i = 0; i < optional.length; ++i) {
            if (!meta[i].getClass().equals(optional[i])) {
                return false;
            }
        }
        return true;
    }
}
