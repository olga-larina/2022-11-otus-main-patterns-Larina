package ru.otus.spacebattle.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.spacebattle.domain.UObject;
import ru.otus.spacebattle.interpreter.Interpreter;
import ru.otus.spacebattle.ioc.IoC;
import ru.otus.spacebattle.ioc.ScopeBasedStrategy;

import java.util.function.Function;

import static org.mockito.Mockito.*;

@DisplayName("Команда-интерпретатор должна")
class InterpretCommandTest {

    private String gameId;
    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        // инициализация IoC
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy();
        (scopeBasedStrategy.new InitScopeBasedIoCCommand()).execute();

        // инициализация игры
        gameId = "game";
        IoC.resolve("Games.Create", gameId);

        // инициализация интерпретатора
        interpreter = mock(Interpreter.class);
        ((Command) IoC.resolve("IoC.Register", "Interpreter.Command.Execute", (Function<Object[], Object>) args -> {
            if (gameId.equals(args[0])) {
                return interpreter.interpret((UObject) args[1]);
            } else {
                return null;
            }
        })).execute();
    }

    @DisplayName("Вызывать интерпретацию приказа и класть команду в очередь")
    @Test
    public void shouldCallInterpreterAndAddCommandToQueue() {
        UObject order = mock(UObject.class);
        Command command = mock(Command.class);
        when(interpreter.interpret(refEq(order))).thenReturn(command);
        new InterpretCommand(gameId, order).execute();
        verify(command, times(1)).execute();
    }

}

