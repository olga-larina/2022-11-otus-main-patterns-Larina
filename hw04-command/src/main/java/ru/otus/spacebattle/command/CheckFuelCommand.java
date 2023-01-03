package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.FuelCheckable;
import ru.otus.spacebattle.exception.CommandException;

/**
 * Команда по проверке уровня топлива
 */
public class CheckFuelCommand implements Command {
    private final FuelCheckable fuelCheckable;

    public CheckFuelCommand(FuelCheckable fuelCheckable) {
        this.fuelCheckable = fuelCheckable;
    }

    @Override
    public void execute() {
        if (fuelCheckable == null) {
            throw new CommandException("Object is null");
        }
        if (fuelCheckable.getFuelLevel() < fuelCheckable.getFuelBurnVelocity()) {
            throw new CommandException("Not enough fuel");
        }
    }
}
