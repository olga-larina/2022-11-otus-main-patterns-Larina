package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Класс решения квадратного уравнения QuadraticEquation должен")
class QuadraticEquationTest {

    private static final double PRECISION = 1e-6;

    private QuadraticEquation quadraticEquation;

    @BeforeEach
    void setUp() {
        quadraticEquation = new QuadraticEquation(PRECISION);
    }

    @DisplayName("Возвращать пустой массив, если D < 0")
    @Test
    public void shouldHaveNoRootsWhenDiscriminantIsLessZero() {
        double a = 1d;
        double b = 0d;
        double c = 1d;
        double[] result = quadraticEquation.solve(a, b, c);
        assertThat(result.length).isZero();
    }

    @DisplayName("Возвращать два корня кратности 1, если D > 0")
    @Test
    public void shouldHaveTwoDifferentRootsWhenDiscriminantIsMoreZero() {
        double a = 1d;
        double b = 0d;
        double c = -1d;
        double[] result = quadraticEquation.solve(a, b, c);
        assertThat(result.length).isEqualTo(2);
        assertThat(result[0]).isEqualTo(1, withPrecision(PRECISION));
        assertThat(result[1]).isEqualTo(-1, withPrecision(PRECISION));
    }

    @DisplayName("Возвращать один корень кратности 2, если D == 0")
    @Test
    public void shouldHaveTwoSameRootsWhenDiscriminantIsEqualZero() {
        double a = 1d + 1e-10;
        double b = 2d + 1e-10;
        double c = 1d + 1e-10;
        double[] result = quadraticEquation.solve(a, b, c);
        assertThat(result.length).isEqualTo(2);
        assertThat(result[0]).isEqualTo(-1, withPrecision(PRECISION));
        assertThat(result[1]).isEqualTo(-1, withPrecision(PRECISION));
    }

    @DisplayName("Бросает исключение, если коэффициент a == 0")
    @Test
    public void shouldThrowExceptionWhenCoefficientAIsEqualZero() {
        double a = PRECISION / 2d;
        double b = 2d;
        double c = 1d;
        assertThatThrownBy(() -> {
            quadraticEquation.solve(a, b, c);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Coefficient a can not equal zero");
    }

    @DisplayName("Бросает исключение, если какой-либо коэффициент является бесконечностью")
    @Test
    public void shouldThrowExceptionWhenCoefficientIsInfinite() {
        for (double[] coefficients: new double[][] {
            new double[] {Double.POSITIVE_INFINITY, 1d, 1d},
            new double[] {1d, Double.POSITIVE_INFINITY, 1d},
            new double[] {1d, 1d, Double.POSITIVE_INFINITY},
            new double[] {Double.NEGATIVE_INFINITY, 1d, 1d},
            new double[] {1d, Double.NEGATIVE_INFINITY, 1d},
            new double[] {1d, 1d, Double.NEGATIVE_INFINITY}
        }) {
            assertThatThrownBy(() -> {
                quadraticEquation.solve(coefficients[0], coefficients[1], coefficients[2]);
            }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Coefficients can not be infinite");
        }
    }

    @DisplayName("Бросает исключение, если какой-либо коэффициент является NaN")
    @Test
    public void shouldThrowExceptionWhenCoefficientIsNaN() {
        for (double[] coefficients: new double[][] {
            new double[] {Double.NaN, 1d, 1d},
            new double[] {1d, Double.NaN, 1d},
            new double[] {1d, 1d, Double.NaN}
        }) {
            assertThatThrownBy(() -> {
                quadraticEquation.solve(coefficients[0], coefficients[1], coefficients[2]);
            }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Coefficients can not be NaN");
        }
    }

}