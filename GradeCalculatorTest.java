import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GradeCalculator {

    private static final int MIN_GRADE = 1;
    private static final int MAX_GRADE = 5;

    public double calculateAverage(List<Integer> grades) {
        if (grades == null || grades.isEmpty()) {
            throw new IllegalArgumentException("Список оценок не может быть null или пустым");
        }

        long sum = 0;
        for (Integer grade : grades) {
            if (grade == null) {
                throw new IllegalArgumentException("Оценка в списке не может быть null");
            }
            if (grade < MIN_GRADE || grade > MAX_GRADE) {
                throw new IllegalArgumentException("Оценка выходит за допустимый диапазон [1..5]: " + grade);
            }
            sum += grade;
        }

        return (double) sum / grades.size();
    }
}

public class GradeCalculatorTest {

    private final GradeCalculator calculator = new GradeCalculator();

    @Test
    void standardGradesTest() {
        List<Integer> grades = Arrays.asList(3, 4, 5, 4);
        assertEquals(4.0, calculator.calculateAverage(grades), 0.001);
    }

    @Test
    void boundaryGradeValuesTest() {
        List<Integer> minGrades = Arrays.asList(1, 1, 1);
        List<Integer> maxGrades = Arrays.asList(5, 5, 5);
        List<Integer> mixedBoundaries = Arrays.asList(1, 5);

        assertEquals(1.0, calculator.calculateAverage(minGrades), 0.001);
        assertEquals(5.0, calculator.calculateAverage(maxGrades), 0.001);
        assertEquals(3.0, calculator.calculateAverage(mixedBoundaries), 0.001);
    }

    @Test
    void collectionSizeBoundariesTest() {
        assertEquals(4.0, calculator.calculateAverage(Collections.singletonList(4)), 0.001);

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(Collections.emptyList());
        });
    }

    @Test
    void outOfRangeGradesThrowTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(Arrays.asList(4, 0, 5));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(Arrays.asList(3, -2, 4));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(Arrays.asList(5, 6));
        });
    }

    @Test
    void nullInputsThrowTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculateAverage(Arrays.asList(4, null, 5));
        });
    }
}
