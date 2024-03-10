package rule.annotation;

import rule.definition.TickActionRule;
import util.ITriConsumer;
import util.ITriPredicate;
import rule.impl.Version3;
import state.State;
import state.board.Position;
import state.board.unit.Tank;
import util.Pair;
import util.ReflectionUtil;
import util.Triple;

import java.lang.annotation.Annotation;
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

        Map<String, Method> methodMap = new HashMap<>();
        Map<String, Method> predicateMap = new HashMap<>();
        Map<String, Pair<Class<?>, BiConsumer<?, State>>> enforceableRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiConsumer<?, State>>> tickRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiConsumer<?, State>>> conditionalRules = new HashMap<>();
        Map<String, Triple<Class<?>, Class<?>, ITriConsumer<?, ?, State>>> playerRules = new HashMap<>();
        Map<String, Pair<Class<?>, BiPredicate<?, State>>> conditionalConditions = new HashMap<>();
        Map<String, Triple<Class<?>, Class<?>, ITriPredicate<?, ?, State>>> playerConditions = new HashMap<>();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length != 1) {
                System.err.format("Method `%s` does not have exactly one annotation\n", method.getName());
            }
            switch (annotations[0]) {
                case RuleEnforcerFunction function -> {
                    validateAddMethod(methodMap, function.id(), method);
                    BiConsumer<?, State> consumer = ReflectionUtil.toBiConsumer(method);
                    enforceableRules.put(function.id(), Pair.of(method.getParameters()[0].getType(), consumer));
                }
                case RuleTickFunction function -> {
                    validateAddMethod(methodMap, function.id(), method);
                    BiConsumer<?, State> consumer = ReflectionUtil.toBiConsumer(method);
                    tickRules.put(function.id(), Pair.of(method.getParameters()[0].getType(), consumer));
                }
                case RuleConditionalFunction function -> {
                    validateAddMethod(methodMap, function.id(), method);
                    BiConsumer<?, State> consumer = ReflectionUtil.toBiConsumer(method);
                    conditionalRules.put(function.id(), Pair.of(method.getParameters()[0].getType(), consumer));
                }
                case RulePlayerFunction function -> {
                    validateAddMethod(methodMap, function.id(), method);
                    ITriConsumer<? , ?, State> consumer = ReflectionUtil.toTriConsumer(method);
                    Parameter[] params = method.getParameters();
                    playerRules.put(function.id(), Triple.of(params[0].getType(), params[1].getType(), consumer));
                }
                case RuleConditionalPredicate function -> {
                    validateAddMethod(predicateMap, function.id(), method);
                    BiPredicate<?, State> predicate = ReflectionUtil.toBiPredicate(method);
                    conditionalConditions.put(function.id(), Pair.of(method.getParameters()[0].getType(), predicate));
                }
                case RulePlayerPredicate function -> {
                    validateAddMethod(predicateMap, function.id(), method);
                    ITriPredicate<? , ?, State> predicate = ReflectionUtil.toTriPredicate(method);
                    Parameter[] params = method.getParameters();
                    playerConditions.put(function.id(), Triple.of(params[0].getType(), params[1].getType(), predicate));
                }
                default -> throw new IllegalStateException("Unexpected: " + annotations[0].annotationType().getName());
            }
        }

        for (String key : enforceableRules.keySet()) {
            Pair<Class<?>, BiConsumer<?, State>> value = tickRules.get(key);
            // Without having the type of the parameter to the function available, we cannot use the generic function
            // but at this point we can guarantee that the rule will be made correctly
            // TODO try to make this work with generics
            description.getTickRules().put(value.left(), new TickActionRule(value.right()));
        }

        ((BiConsumer<Tank, State>) tickRules.get("testrule").right()).accept(
                new Tank(new Position(0,0), 0, 0, 0, 0),
                new State(10, 10, new HashSet<>()));

        rulesets.put(version, description);
    }

    private static void validateAddMethod(Map<String, Method> methodMap, String id, Method method) {
        if (methodMap.containsKey(id)) {
            System.err.format("\n");
            System.exit(1);
        } else {
            methodMap.put(id, method);
        }
    }

    public static Optional<RulesetDescription> getRuleset(int version) {
        return Optional.ofNullable(rulesets.get(version));
    }

}
