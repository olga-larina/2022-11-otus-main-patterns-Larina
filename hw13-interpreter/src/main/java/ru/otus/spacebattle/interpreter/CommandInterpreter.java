package ru.otus.spacebattle.interpreter;

import ru.otus.spacebattle.command.Command;
import ru.otus.spacebattle.domain.UObject;
import ru.otus.spacebattle.exception.InterpretException;
import ru.otus.spacebattle.ioc.IoC;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandInterpreter implements Interpreter {

    private final String gameId;

    public CommandInterpreter(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public Command interpret(UObject order) {
        // получение действия
        Object actionObj = order.getProperty("action");
        Class<Command> action = null;
        if (actionObj != null) {
            action = IoC.resolve(String.format("Games.%s.Actions.Types.Get", gameId), actionObj.toString());
        }
        if (action == null) {
            throw new InterpretException("Action not found");
        }

        // проверяем наличие параметра с id объекта (может отсутствовать для других типов приказов)
        // если присутствует, то проверяем в скоупе игрока наличие такого объекта (отдавать приказы чужим объектам нельзя)
        Object idObj = order.getProperty("id");
        UObject object;
        if (idObj != null) {
            object = IoC.resolve(String.format("Games.%s.Objects.Get", gameId), idObj.toString());
            if (object == null) {
                throw new InterpretException("Object not found");
            }
        } else {
            object = null;
        }

        // отбираем все требуемые для действия параметры
        // игровой объект может не присутствовать среди них => будут обрабатываться любые приказы
        Constructor<?>[] constructors = action.getConstructors();
        Field[] fields = action.getDeclaredFields();
        List<Object> actionParameters = new ArrayList<>();
        if (constructors.length > 0) {
            for (Parameter parameter : constructors[0].getParameters()) {
                Optional<Field> field = Arrays.stream(fields).filter(f -> f.getType() == parameter.getType()).findFirst();
                field.ifPresent(f -> {
                    if (f.getName().equalsIgnoreCase("object")) {
                        actionParameters.add(object);
                    } else {
                        actionParameters.add(order.getProperty(f.getName()));
                    }
                });
            }
        }

        // получаем команду с параметрами
        Command command = IoC.resolve(String.format("Games.%s.Actions.Commands.Get", gameId), actionObj.toString(), actionParameters.toArray());
        if (command == null) {
            throw new InterpretException("Command not found");
        }

        return command;
    }
}
