package pro.trevor.tankgame.util;

import pro.trevor.tankgame.state.State;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Provides conversion functions to wrap Method objects as functional interfaces (e.g., BiConsumer).
 * These functions expect return and parameter types with the functional interface specified.
 * These functions will explicitly error and stop the program if there is an issue with the conversion.
 */
public class ReflectionUtil {

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

    public static <T> BiConsumer<T, State> toBiConsumer(Method method) {
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

    public static <T, U> ITriConsumer<T, U, State> toTriConsumer(Method method) {
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

    public static <T> BiPredicate<T, State> toBiPredicate(Method method) {
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

    public static <T, U, V> ITriPredicate<T, U, V> toTriPredicate(Method method) {
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
