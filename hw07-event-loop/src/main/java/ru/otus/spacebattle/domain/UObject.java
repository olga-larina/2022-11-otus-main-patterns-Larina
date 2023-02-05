package ru.otus.spacebattle.domain;

public interface UObject {

    Object getProperty(String key);

    void setProperty(String key, Object value);

}
