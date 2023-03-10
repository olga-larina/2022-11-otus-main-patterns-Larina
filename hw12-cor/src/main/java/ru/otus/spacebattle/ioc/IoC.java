package ru.otus.spacebattle.ioc;

import ru.otus.spacebattle.command.Command;

/**
 * IoC контейнер
 */
@SuppressWarnings("unchecked")
public class IoC {

    private static final Strategy defaultStrategy = new DefaultStrategy();
    private static Strategy strategy = defaultStrategy;

    private IoC() {
    }

    public static <T> T resolve(String key, Object... args) {
        return (T) strategy.resolve(key, args);
    }

    /**
     * Стратегия по умолчанию
     */
    static class DefaultStrategy implements Strategy {

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
    static class SetupStrategyCommand implements Command {

        private final Strategy newStrategy;

        public SetupStrategyCommand(Strategy newStrategy) {
            this.newStrategy = newStrategy;
        }

        @Override
        public void execute() {
            IoC.strategy = newStrategy;
        }
    }
}
