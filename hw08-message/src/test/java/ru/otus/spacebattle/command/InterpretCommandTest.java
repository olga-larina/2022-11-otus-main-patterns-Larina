package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.UObject;
import ru.otus.spacebattle.handler.queue.QueueHandler;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Команда-интерпретатор должна")
class InterpretCommandTest {

    private String gameId;
    private String objectId;
    private UObject object;
    private CommandQueue queue;
    private String moveId;
    private TestCommand moveCommand;
    private String flyId;
    private TestCommand flyCommand;

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // инициализация обработчика очереди (мок)
        QueueHandler queueHandler = mock(QueueHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Queue.Handler", (Function<Object[], Object>) args -> queueHandler)).execute();

        // инициализация операций
        moveId = "Move";
        ((Command) IoC.resolve("IoC.Register", moveId, (Function<Object[], Object>) args -> {
            moveCommand = spy(new MoveCommand((UObject) args[0], (Object[]) args[1]));
            return moveCommand;
        })).execute();
        flyId = "Fly";
        ((Command) IoC.resolve("IoC.Register", flyId, (Function<Object[], Object>) args -> {
            flyCommand = spy(new FlyCommand((UObject) args[0], (Object[]) args[1]));
            return flyCommand;
        })).execute();

        // подменяем зависимость очереди
        ((Command) IoC.resolve("IoC.Register", "Games.CreateQueue", (Function<Object[], Object>) args1 -> queue)).execute();

        // инициализация игры, подменяем очередь
        gameId = "game";
        objectId = "123";
        object = mock(UObject.class);
        queue = mock(CommandQueue.class);
        IoC.resolve("Games.Create", gameId);
        IoC.resolve(String.format("Games.%s.Objects.Add", gameId), objectId, object);
        IoC.resolve(String.format("Games.%s.AllowedOperations.Add", gameId), moveId);
//        ((Command) IoC.resolve("IoC.Register", String.format("Games.%s.CommandQueue", gameId), (Function<Object[], Object>) args1 -> queue)).execute();
    }

    @DisplayName("Отрабатывать успешно с корректными данными")
    @Test
    public void shouldProcessWithCorrectMessage() {
        Object[] args = new Object[]{ "123", 1 };
        new InterpretCommand(gameId, objectId, moveId, args).execute();
        verify(queue, times(1)).addLast(refEq(moveCommand));
        assertThat(moveCommand.obj).isSameAs(object);
        assertThat(moveCommand.args).isSameAs(args);
    }

    @DisplayName("Бросать ошибку, если нет такого объекта")
    @Test
    public void shouldThrowExceptionIfObjectNotFound() {
        Object[] args = new Object[]{ "123", 1 };
        assertThatThrownBy(() -> {
            new InterpretCommand(gameId, "567", moveId, args).execute();
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Object not found");
    }

    @DisplayName("Бросать ошибку, если операция не разрешена")
    @Test
    public void shouldThrowExceptionIfOperationNotAllowed() {
        Object[] args = new Object[]{ "123", 1 };
        assertThatThrownBy(() -> {
            new InterpretCommand(gameId, objectId, flyId, args).execute();
        }).isInstanceOf(IllegalStateException.class).hasMessage("Operation not allowed");
    }

    private abstract static class TestCommand implements Command {

        protected final UObject obj;
        private final Object[] args;

        TestCommand(UObject obj, Object[] args) {
            this.obj = obj;
            this.args = args;
        }

        @Override
        public void execute() {

        }
    }

    private static class MoveCommand extends TestCommand {

        MoveCommand(UObject obj, Object[] args) {
            super(obj, args);
        }
    }

    private static class FlyCommand extends TestCommand {

        FlyCommand(UObject obj, Object[] args) {
            super(obj, args);
        }
    }
}

