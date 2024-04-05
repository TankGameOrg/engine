package pro.trevor.tankgame.rule.definition.player;

import org.json.JSONObject;
import pro.trevor.tankgame.rule.type.IPlayerElement;
import pro.trevor.tankgame.state.State;
import pro.trevor.tankgame.util.*;

import java.util.Arrays;

public class PlayerActionRule<T extends IPlayerElement> implements IPlayerRule<T> {

    private final String name;
    private final IVarTriPredicate<State, T, Object> predicate;
    private final IVarTriConsumer<State, T, Object> consumer;
    private Class<?>[] paramTypes;
    private String[] paramNames;

    public PlayerActionRule(String name, IVarTriPredicate<State, T, Object> predicate, IVarTriConsumer<State, T, Object> consumer) {
        this.name = name;
        this.predicate = predicate;
        this.consumer = consumer;
        this.paramTypes = new Class[0];
        this.paramNames = new String[0];
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
                error.put("subject", subject.getPlayer());
            }

            System.out.println(error.toString(2));
            throw new Error(String.format("Failed to apply `%s` to `%s` given `%s`", name, subject, Arrays.toString(meta)));
        }
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
    public Class<?>[] paramTypes() {
        return paramTypes;
    }

    @Override
    public String[] paramNames() {
        return paramNames;
    }

    public PlayerActionRuleInternal<T> withParamTypes(Class<?>... paramTypes) {
        this.paramTypes = paramTypes;
        return new PlayerActionRuleInternal<>(this);
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
    public static class PlayerActionRuleInternal<T extends IPlayerElement> {

        private final PlayerActionRule<T> rule;
        private PlayerActionRuleInternal(PlayerActionRule<T> rule) {
            this.rule = rule;
        }

        public PlayerActionRule<T> withParamNames(String... paramNames) {
            assert paramNames.length == rule.paramTypes.length;
            rule.paramNames = paramNames;
            return rule;
        }
    }
}
