package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.handler.queue.QueueHandler;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@DisplayName("Команда обработки очереди в отдельном потоке должна")
class ThreadQueueProcessCommandTest {

    private QueueHandler queueHandler;

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // инициализация обработчика очереди (мок)
        queueHandler = mock(QueueHandler.class);
        ((Command) IoC.resolve("IoC.Register", "Queue.Handler", (Function<Object[], Object>) args -> queueHandler)).execute();
    }

    @DisplayName("Запускать команду по обработке очереди в отдельном потоке")
    @Test
    public void shouldStartQueueProcessCommandInOtherThread() throws InterruptedException {
        CountDownLatch queueHandlerInvoked = new CountDownLatch(1);
        AtomicReference<String> queueHandlerThreadName = new AtomicReference<>();

        doAnswer(invocation -> {
            queueHandlerThreadName.set(Thread.currentThread().getName());
            queueHandlerInvoked.countDown();
            return null;
        }).when(queueHandler).handle();

        ThreadQueueProcessCommand command = new ThreadQueueProcessCommand();
        command.execute();

        boolean invoked = queueHandlerInvoked.await(2, TimeUnit.SECONDS);
        assertThat(invoked).isEqualTo(true);

        assertThat(queueHandlerThreadName.get())
            .isNotNull()
            .isNotEqualTo(Thread.currentThread().getName());
    }

}

