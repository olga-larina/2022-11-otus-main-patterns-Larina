package ru.otus.spacebattle.adapter;

import org.joor.Reflect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.Movable;
import ru.otus.spacebattle.domain.UObject;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Генератор адаптеров должен")
public class AdapterGeneratorImplTest {

    private AdapterGeneratorImpl adapterGenerator;
    private UObject uObject;

    @BeforeEach
    void setUp() {
        adapterGenerator = new AdapterGeneratorImpl();
        uObject = mock(UObject.class);
    }

    @DisplayName("Использовать кэш при генерации адаптеров")
    @Test
    public void shouldUseCacheWhenCreatingAdapter() throws NoSuchFieldException, IllegalAccessException {
        Field adaptersField = AdapterGeneratorImpl.class.getDeclaredField("adapters");
        adaptersField.setAccessible(true);
        Map<String, Reflect> adapters = (Map<String, Reflect>) adaptersField.get(adapterGenerator);

        Movable movable1 = adapterGenerator.resolve(Movable.class, uObject);
        Reflect reflect1 = adapters.get(Movable.class.getName());

        Movable movable2 = adapterGenerator.resolve(Movable.class, uObject);
        Reflect reflect2 = adapters.get(Movable.class.getName());
        assertThat(reflect2).isSameAs(reflect1);
    }

    @DisplayName("Генерировать корректный адаптер")
    @Test
    public void shouldGenerateValidAdapter() {
        Movable movable = adapterGenerator.resolve(Movable.class, uObject);
        assertThat(movable.getClass().getName()).isEqualTo("ru.otus.spacebattle.adapter.MovableAdapter");
        assertThat(movable.getClass().getDeclaredMethods().length).isEqualTo(3);
    }
}

