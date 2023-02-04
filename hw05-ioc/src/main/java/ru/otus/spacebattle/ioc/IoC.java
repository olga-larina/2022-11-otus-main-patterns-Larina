package ru.otus.spacebattle.ioc;

interface IoC {

    <T> T resolve(String key, Object... args);
}
