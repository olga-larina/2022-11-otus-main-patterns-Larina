package ru.otus.spacebattle.handler.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.command.*;
import ru.otus.spacebattle.handler.exception.ExceptionHandler;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;
import ru.otus.spacebattle.state.DefaultState;
import ru.otus.spacebattle.state.MoveToState;

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

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // очередь команд
        commandQueue = mock(CommandQueue.class);

        // базовая стратегия обработки исключений
        exceptionHandler = mock(ExceptionHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Exception.Handler", (Function<Object[], Object>) args1 -> exceptionHandler)).execute();
    }

    @DisplayName("Обрабатывать очередь бесконечно по умолчанию в режиме Default")
    @Test
    public void shouldProcessForeverByDefaultInDefaultState() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(5);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @DisplayName("Продолжать обрабатывать очередь, если команда бросает исключение, в режиме Default")
    @Test
    public void shouldContinueProcessQueueWhenExceptionIsThrownInDefaultState() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

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

    @DisplayName("Продолжать обрабатывать очередь, если очередь пуста, в режиме Default")
    @Test
    public void shouldProcessQueueEvenWhenEmptyInDefaultState() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command).thenReturn(command).thenReturn(null).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(command).execute();

        // запускаем в отдельном потоке
        new Thread(() -> basicQueueHandler.handle()).start();

        boolean invokedCommand = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedCommand).isEqualTo(true);
    }

    @DisplayName("Завершать выполнение сразу при HardStop в режиме Default")
    @Test
    public void shouldFinishImmediatelyWhenHardStopInDefaultState() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

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

        // посылаем hard stop -> выполнение прекращается (несмотря на наличие command в очереди)
        when(commandQueue.readFirst()).thenReturn(new HardStopCommand());
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @DisplayName("Завершать выполнение сразу при HardStop в режиме MoveTo")
    @Test
    public void shouldFinishImmediatelyWhenHardStopInMoveToState() throws InterruptedException {
        // создаём очередь с режимом MoveTo
        CommandQueue otherQueue = mock(CommandQueue.class);
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue);

        // очередь всегда возвращает command
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));

        // запускаем в отдельном потоке
        CountDownLatch queueHandlerStopped = new CountDownLatch(1);

        Thread thread = new Thread(() -> {
            basicQueueHandler.handle();
            queueHandlerStopped.countDown();
        });
        thread.start();

        // проверяем, что command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        // посылаем hard stop -> выполнение прекращается (несмотря на наличие command в очереди)
        when(commandQueue.readFirst()).thenReturn(new HardStopCommand()).thenReturn(command);
        boolean invokedQueueHandler = queueHandlerStopped.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

    @DisplayName("Переходить в режим MoveTo при получении команды Move")
    @Test
    public void shouldSwitchToMoveStateWhenReceivedMoveCommand() throws InterruptedException {
        // инициализация обработчика очереди с обычным режимом обработки команд
        basicQueueHandler = new BasicQueueHandler(new DefaultState(), commandQueue);

        // очередь сначала переходит в режим MoveTo, а затем всегда возвращает command
        Command command = mock(Command.class);
        CommandQueue otherQueue = mock(CommandQueue.class);
        Command moveToCommand = spy(new MoveToCommand(otherQueue));
        when(commandQueue.readFirst()).thenReturn(moveToCommand).thenReturn(command);

        CountDownLatch moveToCommandInvoked = new CountDownLatch(1);
        CountDownLatch commandInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            moveToCommandInvoked.countDown();
            return invocation.callRealMethod();
        }).when(moveToCommand).execute();
        doAnswer(invocation -> {
            commandInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));

        // запускаем в отдельном потоке
        Thread thread = new Thread(() -> basicQueueHandler.handle());
        thread.start();

        // проверяем, что обработалась команда MoveToCommand
        boolean moveToInvoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(moveToInvoked).isEqualTo(true);

        // проверяем, что потом command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);
    }

    @DisplayName("Переходить в режим Default при получении команды Run")
    @Test
    public void shouldSwitchToDefaultStateWhenReceivedRunCommand() throws InterruptedException {
        // создаём очередь с режимом MoveTo
        CommandQueue otherQueue = mock(CommandQueue.class);
        basicQueueHandler = new BasicQueueHandler(new MoveToState(otherQueue), commandQueue);

        // очередь всегда возвращает command
        Command command = mock(Command.class);
        when(commandQueue.readFirst()).thenReturn(command);

        CountDownLatch commandMoveInvoked = new CountDownLatch(3);
        CountDownLatch commandExecInvoked = new CountDownLatch(3);

        doAnswer(invocation -> {
            commandMoveInvoked.countDown();
            return null;
        }).when(otherQueue).addLast(eq(command));
        doAnswer(invocation -> {
            commandExecInvoked.countDown();
            return null;
        }).when(command).execute();

        // запускаем в отдельном потоке
        Thread thread = new Thread(() -> basicQueueHandler.handle());
        thread.start();

        // проверяем, что command всё время возвращаются и кладутся в другую очередь
        boolean invoked = commandMoveInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        // посылаем run, а затем снова command -> выполнение продолжается в обычном режиме
        when(commandQueue.readFirst()).thenReturn(new RunCommand()).thenReturn(command);
        boolean invokedQueueHandler = commandExecInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invokedQueueHandler).isEqualTo(true);
    }

}