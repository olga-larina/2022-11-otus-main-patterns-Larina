package ru.otus.spacebattle;

import ru.otus.spacebattle.adapter.AdapterGenerator;
import ru.otus.spacebattle.adapter.AdapterGeneratorImpl;
import ru.otus.spacebattle.domain.Movable;
import ru.otus.spacebattle.domain.UObject;

public class Main {
    public static void main(String[] args) {
        AdapterGenerator adapterGenerator = new AdapterGeneratorImpl();
//        adapterGenerator.generate(Movable.class);
        UObject object = new UObject() {
            @Override
            public Object getProperty(String key) {
                System.out.println("get " + key);
                return null;
            }

            @Override
            public void setProperty(String key, Object value) {
                System.out.println("set " + key + " " + value);

            }
        };
        Movable resolve1 = adapterGenerator.resolve(Movable.class, object);
        Movable resolve2 = adapterGenerator.resolve(Movable.class, object);
    }
}
