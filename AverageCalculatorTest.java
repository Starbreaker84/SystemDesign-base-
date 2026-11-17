import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AverageCalculator {

    public double calculateAverage(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }

        long sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return (double) sum / numbers.length;
    }
}

public class AverageCalculatorTest {

    @Test
    void positiveNumbersTest() {
        AverageCalculator calculator = new AverageCalculator();
        int[] numbers = {10, 20, 30};
        assertEquals(20.0, calculator.calculateAverage(numbers), 0.001);
    }

    @Test
    void negativeAndMixedNumbersTest() {
        AverageCalculator calculator = new AverageCalculator();
        int[] numbers = {-10, 0, 10, 20};
        assertEquals(5.0, calculator.calculateAverage(numbers), 0.001);
    }

    @Test
    void largeNumbersWithoutOverflowTest() {
        AverageCalculator calculator = new AverageCalculator();
        int[] numbers = {2_000_000_000, 2_000_000_000};
        assertEquals(2_000_000_000.0, calculator.calculateAverage(numbers), 0.001);
    }

    @Test
    void nullArrayThrowsTest() {
        AverageCalculator calculator = new AverageCalculator();
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(null);
        });
    }

    // Не смотря на то, что test coverage будет на 100%,
    // остаётся один крайний случай с пустым массивом,
    // при котором деление на 0 вызовет ошибку.
}
