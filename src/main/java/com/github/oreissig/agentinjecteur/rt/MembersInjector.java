package com.github.oreissig.agentinjecteur.rt;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

// TODO implement different contexts (spring, guice, ...)
public class MembersInjector {

    private static final Map<Class, Provider<?>> providers = new HashMap<>();

    public static <T> T get(Class<T> type/*, Qualifier... qualifiers*/) {
        return (T) providers.getOrDefault(type, () -> {
            throw new RuntimeException("no binding registered for " + type.getName());
        }).get();
    }

    public static <T> void register(Class<T> forType, Provider<T> provider) {
        providers.put(forType, provider);
    }

    public static <T> void register(Class<T> forType, T singleton) {
        providers.put(forType, () -> singleton);
    }
}
