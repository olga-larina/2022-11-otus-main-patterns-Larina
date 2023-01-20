package ru.otus.spacebattle.ioc;

import java.util.function.Function;

/**
 * Корневой скоуп (нет родительского)
 */
class RootScope implements Scope {

    private final Strategy strategy;

    public RootScope(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Object resolve(String key, Object... args) {
        return strategy.resolve(key, args);
    }

    @Override
    public boolean addDependency(String key, Function<Object[], Object> strategy) {
        return false;
    }
}
