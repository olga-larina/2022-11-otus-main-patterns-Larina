package ru.otus.spacebattle.adapter;

import ru.otus.spacebattle.domain.UObject;

public interface AdapterGenerator {

    /**
     * Создание объекта. Если адаптер отсутствует в кэше, то будет сначала сгенерирован
     * @param interfaceType тип интерфейса
     * @param uObject объект исходный
     * @return объект, имплементирующий интерфейс
     */
    <T> T resolve(Class<T> interfaceType, UObject uObject);
}
