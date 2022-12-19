package ru.otus;

/**
 * Координаты
 */
public class Coords {
    private final int x;
    private final int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Coords plus(Coords position, Coords velocity){
        return new Coords(
            position.getX() + velocity.getX(),
            position.getY() + velocity.getY()
        );
    }
}
