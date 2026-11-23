import java.util.Arrays;
import java.util.Random;

public class SimpleMultiThreadProcessing {
    private static final int SIZE = 1_000_000;

    public static void main(String[] args) {
        // Генерируем массив случайных чисел в одну строку
        int[] data = new Random().ints(SIZE, 0, 100).toArray();

        // Параллельно суммируем элементы без ручных потоков и synchronized
        long sum = Arrays.stream(data)
                .parallel()
                .asLongStream() // Защита от переполнения int
                .sum();

        System.out.println("Sum of all elements: " + sum);
    }
}
