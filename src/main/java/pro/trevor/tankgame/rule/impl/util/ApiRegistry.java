package pro.trevor.tankgame.rule.impl.util;

import pro.trevor.tankgame.rule.impl.IApi;

import java.util.HashMap;
import java.util.Optional;

public class ApiRegistry {

    private static final HashMap<String, Class<? extends IApi>> registry = new HashMap<>();

    public static Optional<IApi> getApi(String name) {
        try {
            Class<? extends IApi> api = registry.get(name);
            if (api == null) {
                return Optional.empty();
            }
            return Optional.of(registry.get(name).getConstructor().newInstance());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static void putApi(String name, Class<? extends IApi> api) {
        registry.put(name, api);
    }

}
