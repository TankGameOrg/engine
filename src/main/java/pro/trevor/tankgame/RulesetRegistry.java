package pro.trevor.tankgame;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pro.trevor.tankgame.rule.impl.ruleset.IRulesetRegister;
import pro.trevor.tankgame.util.ReflectionUtil;
import pro.trevor.tankgame.util.RulesetType;

public class RulesetRegistry {
    private static final Map<String, IRulesetRegister> RULESETS = new HashMap<>();

    static {
        List<Class<?>> rulesets = ReflectionUtil.allClassesAnnotatedWith(RulesetType.class, "pro.trevor.tankgame");
        for (Class<?> ruleset : rulesets) {
            Class<? extends IRulesetRegister> rulesetClass = (Class<? extends IRulesetRegister>) ruleset;
            RulesetType rulesetType = ruleset.getAnnotation(RulesetType.class);
            try {
                RULESETS.put(rulesetType.name(), rulesetClass.getConstructor().newInstance());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new Error("Error constructing IRuleset: no matching constructor found for class " +
                        rulesetClass, e);
            } catch (InstantiationException e) {
                throw new Error("Error constructing IRuleset: failed to instantiate class " + rulesetClass, e);
            }
        }
    }

    /**
     * Get a list of the rulesets we support
     */
    public static List<String> getSupportedRulesetNames() {
        return new ArrayList<String>(RULESETS.keySet());
    }

    /**
     * Construct an Api with the specified ruleset if the ruleset exists
     * @param ruleset the ruleset to give the Api
     */
    public static Optional<Api> createApi(String ruleset) {
        if(!RULESETS.containsKey(ruleset)) {
            return Optional.empty();
        }

        return Optional.of(new Api(RULESETS.get(ruleset)));
    }
}
