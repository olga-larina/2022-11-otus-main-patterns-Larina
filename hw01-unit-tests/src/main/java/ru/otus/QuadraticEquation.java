package ru.otus;

public class QuadraticEquation {

    private final double precision;

    public QuadraticEquation(double precision) {
        this.precision = precision;
    }

    public double[] solve(double a, double b, double c) {
        if (Math.abs(a) < precision) {
            throw new IllegalArgumentException("Coefficient a can not equal zero");
        }
        if (Double.isInfinite(a) || Double.isInfinite(b) || Double.isInfinite(c)) {
            throw new IllegalArgumentException("Coefficients can not be infinite");
        }
        if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
            throw new IllegalArgumentException("Coefficients can not be NaN");
        }
        double d = b * b - 4 * a * c;
        if (Math.abs(d) < precision) {
            double x = -b / (2 * a);
            return new double[]{x, x};
        } else if (d < -precision) {
            return new double[0];
        } else {
            return new double[]{(-b + Math.sqrt(d)) / (2 * a), (-b - Math.sqrt(d)) / (2 * a)};
        }
    }
}
