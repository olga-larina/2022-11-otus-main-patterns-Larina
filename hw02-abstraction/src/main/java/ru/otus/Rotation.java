package ru.otus;

/**
 * Поворот
 */
public class Rotation {
    private final Rotatable rotatable;

    public Rotation(Rotatable rotatable) {
        this.rotatable = rotatable;
    }

    public void execute() {
        if (rotatable == null) {
            throw new IllegalStateException("Object is null");
        }
        if (rotatable.getDirectionsNum() == 0) {
            throw new IllegalStateException("Directions num is zero");
        }
        rotatable.setDirection((rotatable.getDirection() + rotatable.getAngularVelocity()) % rotatable.getDirectionsNum());
    }
}
