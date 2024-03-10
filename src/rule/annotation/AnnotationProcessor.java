package rule.annotation;

import rule.definition.TickActionRule;
import rule.definition.player.ITriConsumer;
import rule.definition.player.ITriPredicate;
import rule.impl.Version3;
import state.State;
import state.board.Position;
import state.board.unit.Tank;
import util.Pair;
import util.Trio;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public class AnnotationProcessor {

    private static final Map<Integer, RulesetDescription> rulesets = new HashMap<>();

    static {
        registerClasses(Version3.class);
    }

    private static void registerClasses(Class<?>... classes) {
        for(Class<?> c : classes) {
            registerClass(c);
        }
    }

    private static void registerClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(RuleClass.class)) {
            System.err.format("Class `%s` does not have required `%s` annotation to be processed\n",
                    clazz.getName(), RuleClass.class.getName());
            System.exit(1);
        }
        RulesetDescription description = new RulesetDescription();
        int version = clazz.getAnnotation(RuleClass.class).version();

        Method[] methods = clazz.getDeclaredMethods();

        Map<String, Pair<Class<?>, BiConsumer<?, State>>> enforceableRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiConsumer<?, State>>> tickRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiConsumer<?, State>>> conditionalRules = new HashMap<>();
        Map<String, Trio<Class<?>, Class<?>, ITriConsumer<?, ?, State>>> playerRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiPredicate<?, State>>> conditionalConditions = new HashMap<>();
        Map<String, Trio<Class<?>, Class<?>, ITriPredicate<?, ?, State>>> playerConditions = new HashMap<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RuleFunction.class)) {
                RuleFunction annotation = method.getAnnotation(RuleFunction.class);
                String id = annotation.id();
                switch (annotation.type()) {
                    case ENFORCEABLE -> {
                        BiConsumer<?, State> consumer = toBiConsumer(method);
                        enforceableRules.put(id, Pair.of(method.getParameters()[1].getType(), consumer));
                    }
                    case TICK -> {
                        BiConsumer<?, State> consumer = toBiConsumer(method);
                        tickRules.put(id, Pair.of(method.getParameters()[1].getType(), consumer));
                    }
                    case CONDITIONAL -> {
                        BiConsumer<?, State> consumer = toBiConsumer(method);
                        conditionalRules.put(id, Pair.of(method.getParameters()[1].getType(), consumer));
                    }
                    case PLAYER -> {
                        ITriConsumer<? , ?, State> predicate = toTriConsumer(method);
                        playerRules.put(id, Trio.of(method.getParameters()[1].getType(), method.getParameters()[2].getType(), predicate));
                    }
                }
            } else if (method.isAnnotationPresent(RulePredicateFunction.class)) {
                RulePredicateFunction annotation = method.getAnnotation(RulePredicateFunction.class);
                String id = annotation.id();
                switch (annotation.type()) {
                    case CONDITIONAL -> {
                        BiPredicate<?, State> predicate = toBiPredicate(method);
                        conditionalConditions.put(id, Pair.of(method.getParameters()[1].getType(), predicate));
                    }
                    case PLAYER -> {
                        ITriPredicate<? , ?, State> predicate = toTriPredicate(method);
                        playerConditions.put(id, Trio.of(method.getParameters()[1].getType(), method.getParameters()[2].getType(), predicate));
                    }
                }
            }
        }

        for (String key : tickRules.keySet()) {
            Pair<Class<?>, BiConsumer<?, State>> value = tickRules.get(key);
            // Without having the type of the parameter to the function available, we cannot use the generic function
            // TODO try to make this work correctly
            description.getTickRules().put(value.left(), new TickActionRule(value.right()));
        }

        ((BiConsumer<Tank, State>) tickRules.get("myrule").right()).accept(
                new Tank(new Position(0,0), 0, 0, 0, 0),
                new State(10, 10, new HashSet<>()));

        rulesets.put(version, description);
    }

    public static Optional<RulesetDescription> getRuleset(int version) {
        return Optional.ofNullable(rulesets.get(version));
    }

    // TODO move all functions below to a utility class
    private static void expectParameters(Method method, int argc) {
        assert argc > 0;
        Parameter[] parameters = method.getParameters();
        if (parameters.length != argc) {
            System.err.printf("Method `%s` does not have exactly %d fields\n", method.getName(), argc);
            System.exit(1);
        } else if (!parameters[argc-1].getType().equals(State.class)) {
            System.err.printf("Method `%s` does does not consume a State as its last parameter\n", method.getName());
            System.exit(1);
        }
    }

    private static <T> void expectReturnType(Method method, Class<T> clazz) {
        if (!method.getReturnType().equals(clazz)) {
            System.err.printf("Method `%s` does does not return type `%s` as expected\n",
                    method.getName(), clazz.getName());
            System.exit(1);
        }
    }

    private static <T> BiConsumer<T, State> toBiConsumer(Method method) {
        expectParameters(method, 2);
        expectReturnType(method, void.class);
        return (t, u) -> {
            try {
                method.invoke(null, t, u);
            } catch (Exception e) {
                System.err.printf("Failed to invoke `%s` with argument types `%s` and `%s`\n",
                        method.getName(), t.getClass().getName(), u.getClass().getName());
                e.printStackTrace();
                System.exit(1);
            }
        };
    }

    private static <T, U> ITriConsumer<T, U, State> toTriConsumer(Method method) {
        expectParameters(method, 3);
        expectReturnType(method, void.class);
        return (t, u, v) -> {
            try {
                method.invoke(null, t, u, v);
            } catch (Exception e) {
                System.err.printf("Failed to invoke `%s` with argument types `%s`, `%s`, and `%s`\n",
                        method.getName(), t.getClass().getName(), u.getClass().getName(), v.getClass().getName());
                e.printStackTrace();
                System.exit(1);
            }
        };
    }

    private static <T> BiPredicate<T, State> toBiPredicate(Method method) {
        expectParameters(method, 2);
        expectReturnType(method, boolean.class);
        return (t, u) -> {
            try {
                return (boolean) method.invoke(null, t, u);
            } catch (Exception e) {
                System.err.printf("Failed to invoke `%s` with argument types `%s` and `%s`\n",
                        method.getName(), t.getClass().getName(), u.getClass().getName());
                e.printStackTrace();
                System.exit(1);
            }
            return false;
        };
    }

    private static <T, U, V> ITriPredicate<T, U, V> toTriPredicate(Method method) {
        expectParameters(method, 3);
        expectReturnType(method, boolean.class);
        return (t, u, v) -> {
            try {
                return (boolean) method.invoke(null, t, u);
            } catch (Exception e) {
                System.err.printf("Failed to invoke `%s` with argument types `%s`, `%s`, and `%s`\n",
                        method.getName(), t.getClass().getName(), u.getClass().getName(), v.getClass().getName());
                e.printStackTrace();
                System.exit(1);
            }
            return false;
        };
    }

}
