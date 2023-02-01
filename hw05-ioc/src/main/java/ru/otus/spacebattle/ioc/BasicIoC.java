package ru.otus.spacebattle.ioc;

import ru.otus.spacebattle.command.Command;

/**
 * Базовый IoC контейнер
 */
@SuppressWarnings("unchecked")
class BasicIoC implements IoC {

    private final Strategy defaultStrategy = new DefaultStrategy();
    private Strategy strategy;

    BasicIoC() {
        this.strategy = defaultStrategy;
    }

    @Override
    public <T> T resolve(String key, Object... args) {
        return (T) strategy.resolve(key, args);
    }

    /**
     * Стратегия по умолчанию
     */
    class DefaultStrategy implements Strategy {

        @Override
        public Object resolve(String key, Object... args) {
            if ("IoC.SetupStrategy".equals(key)) {
                return new SetupStrategyCommand((Strategy) args[0]);
            } else if ("IoC.Default".equals(key)) {
                return this;
            } else {
                throw new IllegalArgumentException(String.format("Unknown key %s", key));
            }
        }
    }

    /**
     * Установка текущей стратегии
     */
    class SetupStrategyCommand implements Command {

        private final Strategy newStrategy;

        public SetupStrategyCommand(Strategy newStrategy) {
            this.newStrategy = newStrategy;
        }

        @Override
        public void execute() {
            BasicIoC.this.strategy = newStrategy;
        }
    }
}
