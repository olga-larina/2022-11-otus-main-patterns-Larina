package ru.otus.spacebattle.handler.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.*;
import ru.otus.spacebattle.handler.exception.ExceptionHandler;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Базовая стратегия по обработке очереди должна")
class BasicQueueHandlerTest {

    private BasicQueueHandler basicQueueHandler;
    private ExceptionHandler exceptionHandler;
    private CommandQueue commandQueue;
    private Command emptyCommand;

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // инициализация обработчика очереди
        basicQueueHandler = new BasicQueueHandler();

        // базовая стратегия обработки исключений
        exceptionHandler = mock(ExceptionHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Exception.Handler", (Function<Object[], Object>) args1 -> exceptionHandler)).execute();

        // очередь команд
        commandQueue = mock(CommandQueue.class);
        ((Command) IoC.resolve("IoC.Register", "CommandQueue", (Function<Object[], Object>) args1 -> commandQueue)).execute();

        // базовая стратегия обработки очереди - бесконечная
        new ContinueCommand().execute();

        // пустая команда, которая будет возвращаться в базовой стратегии
        emptyCommand = mock(Command.class);
        ((Command) IoC.resolve("IoC.Register", "CommandQueue.EmptyCommand", (Function<Object[], Object>) args1 -> emptyCommand)).execute();
    }

    @DisplayName("Обрабатывать очередь бесконечно по умолчанию")
    @Test
    public void shouldProcessForeverByDefault() throws InterruptedException {
        CountDownLatch emptyCommandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            emptyCommandInvoked.countDown();
            return null;
        }).when(emptyCommand).execute();

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = emptyCommandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @DisplayName("Продолжать обрабатывать очередь, если команда бросает исключение")
    @Test
    public void shouldContinueProcessQueueWhenExceptionIsThrown() throws InterruptedException {
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            throw new IllegalArgumentException("TEST");
        }).when(command).execute();

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        verify(exceptionHandler, atLeast(4)).handle(any(), any());
    }

    @DisplayName("Обрабатывать очередь команд, а потом - возвращать пустую команду")
    @Test
    public void shouldProcessQueueAndThenReturnEmptyCommand() throws InterruptedException {
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command).thenReturn(command).thenReturn(null);

        CountDownLatch commandInvoked = new CountDownLatch(2);
        CountDownLatch emptyCommandInvoked = new CountDownLatch(2);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        doAnswer(invocation -> {
            emptyCommandInvoked.countDown();
            return null;
        }).when(emptyCommand).execute();

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invokedCommand = commandInvoked.await(2, TimeUnit.SECONDS);
        boolean invokedEmptyCommand = emptyCommandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand).isEqualTo(true);
        assertThat(invokedEmptyCommand).isEqualTo(true);
    }

    @DisplayName("Завершать выполнение сразу при HardStop")
    @Test
    public void shouldFinishImmediatelyWhenHardStop() throws InterruptedException {
        // очередь всегда возвращает command
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        // запускаем в отдельном потоке
        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();

        // проверяем, что действительно всё время возвращается command
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        // посылаем hard stop -> выполнение прекращается
        new HardStopCommand().execute();
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @DisplayName("Завершать выполнение, когда команды закончатся, при SoftStop")
    @Test
    public void shouldFinishIfQueueIsEmptyWhenSoftStop() throws InterruptedException {
        // очередь всегда возвращает command1
        Command command1 = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command1);

        CountDownLatch command1Invoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            command1Invoked.countDown();
            return null;
        }).when(command1).execute();

        // запускаем в отдельном потоке
        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();

        // проверяем, что действительно всё время возвращается command1
        boolean invokedCommand1 = command1Invoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand1).isEqualTo(true);

        // посылаем soft stop -> выполнение продолжается
        new SoftStopCommand().execute();

        // чтобы это проверить, добавляем новый command2 в очередь
        Command command2 = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command2);

        CountDownLatch command2Invoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            command2Invoked.countDown();
            return null;
        }).when(command2).execute();

        // проверяем, что действительно продолжает выполняться
        boolean invokedCommand2 = command2Invoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand2).isEqualTo(true);

        // делаем очередь пустой, после чего выполнение прекращается
        when(commandQueue.readFirst()).thenReturn(null);

        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

}