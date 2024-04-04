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
    private Class<?>[] paramTypes;
    private String[] paramNames;

    public PlayerActionRule(String name, IVarQuadPredicate<State, T, U, Object> predicate, IVarQuadConsumer<State, T, U, Object> consumer) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
        this.paramTypes = new Class[0];
        this.paramNames = new String[0];
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
    public Class<?>[] paramTypes() {
        return paramTypes;
    }

    @Override
    public String[] paramNames() {
        return paramNames;
    }

    public PlayerActionRuleInternal<T, U> withParamTypes(Class<?>... paramTypes) {
        this.paramTypes = paramTypes;
        return new PlayerActionRuleInternal<T, U>(this);
    }

    private boolean validateOptionalTypes(Object[] meta) {
        if (meta.length != paramTypes.length) {
            return false;
        }
        for (int i = 0; i < paramTypes.length; ++i) {
            if (!meta[i].getClass().equals(paramTypes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Internal class used to force the caller of withParamTypes(...) to also call withParamNames(...)
     */
    public static class PlayerActionRuleInternal<T extends IPlayerElement, U> {

        private final PlayerActionRule<T, U> rule;
        private PlayerActionRuleInternal(PlayerActionRule<T, U> rule) {
            this.rule = rule;
        }

        public PlayerActionRule<T, U> withParamNames(String... paramNames) {
            assert paramNames.length == rule.paramTypes.length;
            rule.paramNames = paramNames;
            return rule;
        }
    }
}
