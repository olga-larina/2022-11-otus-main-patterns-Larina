package ru.otus.spacebattle.ioc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.Command;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.otus.spacebattle.ioc.ScopeBasedStrategy.ROOT_SCOPE_NAME;

@DisplayName("IoC контейнер должен")
public class BasicIoCTest {

    private BasicIoC basicIoC;
    private ScopeBasedStrategy scopeBasedStrategy;

    @BeforeEach
    void setUp() {
        basicIoC = new BasicIoC();
        scopeBasedStrategy = new ScopeBasedStrategy(basicIoC);
    }

    @DisplayName("Бросать ошибку при регистрации зависимости, если стратегия не инициализирована")
    @Test
    public void shouldThrowExceptionIfStrategyNotInitialized() {
        assertThatThrownBy(() -> {
            basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Runner((String) args[0]));
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key IoC.Register");
    }

    @DisplayName("Устанавливать пользовательскую стратегию")
    @Test
    public void shouldSetCustomStrategy() {
        // стратегия, которая независимо от ключа возвращает один и тот же объект
        Object object = new Object();
        Strategy myStrategy = new Strategy() {
            @Override
            public Object resolve(String key, Object... args) {
                return object;
            }
        };
        ((Command) basicIoC.resolve("IoC.SetupStrategy", myStrategy)).execute();
        Object test = basicIoC.resolve("TEST");
        assertThat(test).isEqualTo(object);
    }

    @DisplayName("Получать дефолтную корневую стратегию, даже если установлена другая")
    @Test
    public void shouldGetDefaultRootStrategy() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        Strategy defaultStrategy = basicIoC.resolve("IoC.Default");
        assertThat(defaultStrategy).isInstanceOf(BasicIoC.DefaultStrategy.class);
        assertThat(defaultStrategy.resolve("IoC.Default")).isNotNull();
        assertThat(defaultStrategy.resolve("IoC.SetupStrategy", (Strategy) (key, args) -> null)).isInstanceOf(BasicIoC.SetupStrategyCommand.class);
        assertThatThrownBy(() -> {
            basicIoC.resolve("IoC.Resolve", "Test");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key IoC.Resolve");
    }

    @DisplayName("Регистрировать и разрешать зависимости")
    @Test
    public void shouldRegisterAndResolveDependency() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // регистрация зависимостей
        String movableName = "Happy runner";
        Command registerMovable = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Runner((String) args[0]));
        registerMovable.execute();
        Command registerSlowMove = basicIoC.resolve("IoC.Register", "Move", (Function<Object[], Object>) args -> new MoveCommand(basicIoC.resolve("Movable", movableName)));
        registerSlowMove.execute();

        // разрешение зависимостей
        Command moveCommand = basicIoC.resolve("Move");
        assertThat(moveCommand).isInstanceOf(MoveCommand.class);
        Movable movable = ((MoveCommand) moveCommand).movable;
        assertThat(movable).isInstanceOf(Runner.class);
        assertThat(movable.getName()).isEqualTo(movableName);
    }

    @DisplayName("Перезаписывать зависимость с одинаковым именем в том же скоупе")
    @Test
    public void shouldRewriteDependencyInSameScope() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        String movableName1 = "Happy runner";
        String movableName2 = "Happy swimmer";

        // регистрация зависимостей
        Command registerMovable1 = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Runner((String) args[0]));
        registerMovable1.execute();

        // разрешение зависимостей
        Movable movable1 = basicIoC.resolve("Movable", movableName1);
        assertThat(movable1).isInstanceOf(Runner.class);
        assertThat(movable1.getName()).isEqualTo(movableName1);

        // повторная регистрация зависимостей с тем же именем
        Command registerMovable2 = basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Swimmer((String) args[0]));
        registerMovable2.execute();

        // разрешение зависимостей
        Movable movable2 = basicIoC.resolve("Movable", movableName2);
        assertThat(movable2).isInstanceOf(Swimmer.class);
        assertThat(movable2.getName()).isEqualTo(movableName2);
    }

    @DisplayName("Бросать ошибку при переключении на несуществующий скоуп")
    @Test
    public void shouldThrowExceptionIfUseUnknownScope() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        assertThatThrownBy(() -> {
            ((Command) basicIoC.resolve("Scopes.Current.Set", "scopeId")).execute();
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Scope scopeId not found");
    }

    @DisplayName("Получать текущий скоуп, создавать новый, переключаться между скоупами")
    @Test
    public void shouldOperateScopes() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // текущий скоуп == рутовый
        String currentScope = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope).isEqualTo(ROOT_SCOPE_NAME);

        // нельзя создать скоуп с несуществующим родительским
        assertThatThrownBy(() -> {
            ((Command) basicIoC.resolve("Scopes.New", "scopeId", "test")).execute();
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Parent scope scopeId not found");

        // создание нового скоупа
        String newScopeName = "ABC";
        ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName)).execute();

        // нельзя создать скоуп с тем же именем
        assertThatThrownBy(() -> {
            ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName)).execute();
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Scope ABC already exists");

        // установка нового скоупа
        ((Command) basicIoC.resolve("Scopes.Current.Set", newScopeName)).execute();
        String currentScope2 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope2).isEqualTo(newScopeName);

        // переключение обратно
        ((Command) basicIoC.resolve("Scopes.Current.Set", ROOT_SCOPE_NAME)).execute();
        String currentScope3 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope3).isEqualTo(ROOT_SCOPE_NAME);
    }

    @DisplayName("Искать зависимости в родительских скоупах")
    @Test
    public void shouldResolveDependenciesInParentScopes() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
        String movableName = "Happy runner";

        // создание нового скоупа
        String newScopeName1 = "ABC1";
        ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName1)).execute();

        // создание дочернего скоупа
        String newScopeName2 = "ABC2";
        ((Command) basicIoC.resolve("Scopes.New", newScopeName1, newScopeName2)).execute();

        // установка нового скоупа и сохранение зависимости
        ((Command) basicIoC.resolve("Scopes.Current.Set", newScopeName1)).execute();
        String currentScope1 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope1).isEqualTo(newScopeName1);
        ((Command) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Runner((String) args[0]))).execute();

        // переключение на дочерний и поиск зависимости
        ((Command) basicIoC.resolve("Scopes.Current.Set", newScopeName2)).execute();
        String currentScope2 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope2).isEqualTo(newScopeName2);
        Runner runner = basicIoC.resolve("Movable", movableName);
        assertThat(runner).isNotNull();
        assertThat(runner.getName()).isEqualTo(movableName);
    }

    @DisplayName("Не находить зависимости из другого скоупа")
    @Test
    public void shouldNotResolveDependenciesFromOtherScope() {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
        String movableName = "Happy runner";

        // создание нового скоупа
        String newScopeName1 = "ABC1";
        ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName1)).execute();

        // создание другого скоупа (не дочернего)
        String newScopeName2 = "ABC2";
        ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, newScopeName2)).execute();

        // установка нового скоупа и сохранение зависимости
        ((Command) basicIoC.resolve("Scopes.Current.Set", newScopeName1)).execute();
        String currentScope1 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope1).isEqualTo(newScopeName1);
        ((Command) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> new Runner((String) args[0]))).execute();

        // переключение на несвязанный скуп и поиск зависимости
        ((Command) basicIoC.resolve("Scopes.Current.Set", newScopeName2)).execute();
        String currentScope2 = basicIoC.resolve("Scopes.Current");
        assertThat(currentScope2).isEqualTo(newScopeName2);
        assertThatThrownBy(() -> {
            basicIoC.resolve("Movable", movableName);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key Movable");
    }

    @DisplayName("Иметь разные скоупы и зависимости для разных потоков")
    @Test
    public void shouldOperateInDifferentThreads() throws InterruptedException {
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
        String[] movableNames = {"Happy runner", "Happy swimmer"};
        String[] scopeNames = {"Scope1", "Scope2"};

        Function<Integer, Runnable> runnable = i -> {
            return () -> {
                // создание нового скоупа
                ((Command) basicIoC.resolve("Scopes.New", ROOT_SCOPE_NAME, scopeNames[i])).execute();

                // установка нового скоупа и сохранение зависимости
                ((Command) basicIoC.resolve("Scopes.Current.Set", scopeNames[i])).execute();
                String currentScope = basicIoC.resolve("Scopes.Current");
                assertThat(currentScope).isEqualTo(scopeNames[i]);

                ((Command) basicIoC.resolve("IoC.Register", "Movable", (Function<Object[], Object>) args -> {
                    if (i == 0) {
                        return new Runner((String) args[0]);
                    } else {
                        return new Swimmer((String) args[0]);
                    }
                })).execute();

                Movable movable = basicIoC.resolve("Movable", movableNames[i]);
                if (i == 0) {
                    assertThat(movable).isInstanceOf(Runner.class);
                } else {
                    assertThat(movable).isInstanceOf(Swimmer.class);
                }
                assertThat(movable.getName()).isEqualTo(movableNames[i]);
            };
        };

        // запускаем в отдельном потоке
        Thread thread = new Thread(runnable.apply(0));
        thread.start();

        // запускаем в текущем потоке
        runnable.apply(1).run();

        // дожидаемся выполнения потока
        thread.join();

        // ещё раз проверяем
        Movable movable = basicIoC.resolve("Movable", movableNames[1]);
        assertThat(movable).isInstanceOf(Swimmer.class);
        assertThat(movable.getName()).isEqualTo(movableNames[1]);
    }

    private interface Movable {
        void setPosition(int pos);
        int getPosition();

        String getName();
    }

    private static abstract class AbstractMover implements Movable {
        private final String name;
        private int pos;

        AbstractMover(String name) {
            this.name = name;
        }

        @Override
        public void setPosition(int pos) {
            this.pos = pos;
        }

        @Override
        public int getPosition() {
            return pos;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static class Runner extends AbstractMover {

        Runner(String name) {
            super(name);
        }
    }

    private static class Swimmer extends AbstractMover {

        Swimmer(String name) {
            super(name);
        }
    }

    private static class MoveCommand implements Command {

        private final Movable movable;

        MoveCommand(Movable movable) {
            this.movable = movable;
        }

        @Override
        public void execute() {
            movable.setPosition(movable.getPosition() + 1);
        }
    }
}

