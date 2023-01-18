package ru.otus.spacebattle.command;

import ru.otus.spacebattle.domain.FuelBurnable;
import ru.otus.spacebattle.exception.CommandException;

/**
 * Команда по сжиганию топлива
 */
public class BurnFuelCommand implements Command {
    private final FuelBurnable fuelBurnable;

    public BurnFuelCommand(FuelBurnable fuelBurnable) {
        this.fuelBurnable = fuelBurnable;
    }

    @Override
    public void execute() {
        if (fuelBurnable == null) {
            throw new CommandException("Object is null");
        }
        int newLevel = fuelBurnable.getFuelLevel() - fuelBurnable.getFuelBurnVelocity();
        if (newLevel < 0) {
            throw new CommandException("Not enough fuel");
        }
        fuelBurnable.setFuelLevel(newLevel);
    }
}
