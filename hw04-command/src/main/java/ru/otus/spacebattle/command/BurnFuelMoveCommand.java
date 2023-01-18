package ru.otus.spacebattle.command;

import java.util.List;

/**
 * Макрокоманда движения по прямой с расходом топлива (проверка топлива + движение + сжигание топлива)
 */
public class BurnFuelMoveCommand extends MacroCommand {

    public BurnFuelMoveCommand(CheckFuelCommand checkFuelCommand,
                               MoveCommand moveCommand,
                               BurnFuelCommand burnFuelCommand) {
        super(List.of(checkFuelCommand, moveCommand, burnFuelCommand));
    }

}
