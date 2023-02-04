package ru.otus.spacebattle.ioc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.adapter.AdapterGenerator;
import ru.otus.spacebattle.adapter.AdapterGeneratorImpl;
import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.domain.Coords;
import ru.otus.spacebattle.domain.Movable;
import ru.otus.spacebattle.domain.UObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("IoC контейнер должен")
public class IoCTest {

    private UObject uObject;

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();
        uObject = mock(UObject.class);
    }

    @DisplayName("Регистрировать и создавать экземпляр генератора адаптеров один раз в одном потоке")
    @Test
    public void shouldAddAdapterGeneratorOnceInSameThread() {
        AdapterProvider adapterProvider = spy(new AdapterProvider());
        // подменяем зависимость генерации адаптеров
        ((Command) IoC.resolve("IoC.Register", "Adapter.Generator.Create", (Function<Object[], Object>) args -> adapterProvider.generate())).execute();

        assertThatThrownBy(() -> {
            IoC.resolve("Adapter.Generator");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key Adapter.Generator");

        Movable movable1 = IoC.resolve("Adapter", Movable.class, uObject);
        AdapterGenerator adapterGenerator1 = IoC.resolve("Adapter.Generator");
        assertThat(adapterGenerator1).isInstanceOf(AdapterGeneratorImpl.class);

        Movable movable2 = IoC.resolve("Adapter", Movable.class, uObject);
        AdapterGenerator adapterGenerator2 = IoC.resolve("Adapter.Generator");
        assertThat(adapterGenerator2).isInstanceOf(AdapterGeneratorImpl.class).isSameAs(adapterGenerator1);
        verify(adapterProvider, times(1)).generate();
    }

    @DisplayName("Регистрировать и создавать экземпляр генератора адаптеров один раз в разных потоках")
    @Test
    public void shouldAddAdapterGeneratorOnceInDifferentThreads() throws InterruptedException {
        AdapterProvider adapterProvider = spy(new AdapterProvider());
        // подменяем зависимость генерации адаптеров
        ((Command) IoC.resolve("IoC.Register", "Adapter.Generator.Create", (Function<Object[], Object>) args -> adapterProvider.generate())).execute();

        Map<Integer, AdapterGenerator> generators = new ConcurrentHashMap<>();

        Function<Integer, Runnable> runnable = i ->
            () -> {
                Movable movable1 = IoC.resolve("Adapter", Movable.class, uObject);
                AdapterGenerator adapterGenerator1 = IoC.resolve("Adapter.Generator");
                assertThat(adapterGenerator1).isInstanceOf(AdapterGeneratorImpl.class);
                generators.put(i, adapterGenerator1);
            };

        // запускаем в отдельном потоке
        Thread thread1 = new Thread(runnable.apply(0));
        thread1.start();

        // запускаем в отдельном потоке
        Thread thread2 = new Thread(runnable.apply(1));
        thread2.start();

        // запускаем в текущем потоке
        runnable.apply(2).run();

        // дожидаемся выполнения потоков
        thread1.join();
        thread2.join();

        // проверяем, что генератор создан только один раз
        assertThat(generators.size()).isEqualTo(3);
        assertThat(generators.get(0)).isSameAs(generators.get(1)).isSameAs(generators.get(2));
        verify(adapterProvider, times(1)).generate();
    }

    @DisplayName("Генерировать корректный адаптер, который вызывает требуемые методы объекта")
    @Test
    public void shouldGenerateValidAdapterThatCallsValidMethods() {
        Movable movable = IoC.resolve("Adapter", Movable.class, uObject);
        ((Command) IoC.resolve("IoC.Register", "Movable:position.get", (Function<Object[], Object>) args -> ((UObject) args[0]).getProperty("position"))).execute();
        ((Command) IoC.resolve("IoC.Register", "Movable:velocity.get", (Function<Object[], Object>) args -> ((UObject) args[0]).getProperty("velocity"))).execute();
        ((Command) IoC.resolve("IoC.Register", "Movable:position.set", (Function<Object[], Object>) args -> {
            ((UObject) args[0]).setProperty("position", args[1]);
            return null;
        })).execute();

        Coords coords = new Coords(1, 2, 3);

        movable.getPosition();
        verify(uObject, times(1)).getProperty("position");
        verify(uObject, times(0)).getProperty("velocity");
        verify(uObject, times(0)).setProperty("position", coords);

        movable.getVelocity();
        verify(uObject, times(1)).getProperty("position");
        verify(uObject, times(1)).getProperty("velocity");
        verify(uObject, times(0)).setProperty("position", coords);

        movable.setPosition(coords);
        verify(uObject, times(1)).getProperty("position");
        verify(uObject, times(1)).getProperty("velocity");
        verify(uObject, times(1)).setProperty("position", coords);
    }

    private static class AdapterProvider {
        AdapterGenerator generate() {
            return new AdapterGeneratorImpl();
        }
    }
}

