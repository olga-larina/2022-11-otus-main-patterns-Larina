package ru.otus.spacebattle.interpreter;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.domain.UObject;

/**
 * Интерпретатор
 */
public interface Interpreter {

    /**
     * Интерпретация любого приказа игры в команду (с проверкой прав на объект при его наличии)
     * @param object приказ
     * @return команда
     */
    Command interpret(UObject object);
}
