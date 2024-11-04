package P_3;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class kp3prak {
    public static void main(String[] args) throws IOException {
        // ств 3 мас
        int[] array1 = generateRandomArray(15, 25);
        int[] array2 = generateRandomArray(15, 25);
        int[] array3 = generateRandomArray(15, 25);

        System.out.println("Початковий масив 1: " + Arrays.toString(array1));
        System.out.println("Початковий масив 2: " + Arrays.toString(array2));
        System.out.println("Початковий масив 3: " + Arrays.toString(array3));

        // запис мас у файл
        writeArrayToFile(array1, "array1.txt");
        writeArrayToFile(array2, "array2.txt");
        writeArrayToFile(array3, "array3.txt");

        // зчит мас з файл
        int[] loadedArray1 = readArrayFromFile("array1.txt");
        int[] loadedArray2 = readArrayFromFile("array2.txt");
        int[] loadedArray3 = readArrayFromFile("array3.txt");

        // ForkJoinPool для пар вик
        ForkJoinPool pool = new ForkJoinPool();

        // пар обр мас
        int[] processedArray1 = pool.invoke(new MultiplyByThreeTask(loadedArray1));
        int[] processedArray2 = pool.invoke(new FilterEvenNumbersTask(loadedArray2));
        int[] processedArray3 = pool.invoke(new FilterRangeTask(loadedArray3, 10, 175));

        // вив обр мас
        System.out.println("Масив 1 після множення на 3: " + Arrays.toString(processedArray1));
        System.out.println("Масив 2 (лише парні числа): " + Arrays.toString(processedArray2));
        System.out.println("Масив 3 (числа в діапазоні [10; 175]): " + Arrays.toString(processedArray3));

        // сорт мас
        Arrays.sort(processedArray1);
        Arrays.sort(processedArray2);
        Arrays.sort(processedArray3);

        // злиття мас
        int[] finalArray = mergeArrays(processedArray1, processedArray2, processedArray3);

        // вив фін мас
        System.out.println("Фінальний об'єднаний масив: " + Arrays.toString(finalArray));
    }

    // метод генерації мас з випад числ
    public static int[] generateRandomArray(int minSize, int maxSize) {
        Random random = new Random();
        int size = random.nextInt(maxSize - minSize + 1) + minSize;
        return random.ints(size, 0, 1001).toArray();
    }

    // зап мас у файл
    public static void writeArrayToFile(int[] array, String filename) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (int num : array) {
                writer.write(num + " ");
            }
        }
    }

    // зчит мас з файл
    public static int[] readArrayFromFile(String filename) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            return Arrays.stream(reader.readLine().trim().split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
    }

    // клас множ всіх елем на 3
    static class MultiplyByThreeTask extends RecursiveTask<int[]> {
        private final int[] array;

        public MultiplyByThreeTask(int[] array) {
            this.array = array;
        }

        @Override
        protected int[] compute() {
            return Arrays.stream(array).map(num -> num * 3).toArray();
        }
    }

    // клас для фільтр парн чис
    static class FilterEvenNumbersTask extends RecursiveTask<int[]> {
        private final int[] array;

        public FilterEvenNumbersTask(int[] array) {
            this.array = array;
        }

        @Override
        protected int[] compute() {
            return Arrays.stream(array).filter(num -> num % 2 == 0).toArray();
        }
    }

    // клас для фільтр чис [10; 175]
    static class FilterRangeTask extends RecursiveTask<int[]> {
        private final int[] array;
        private final int lowerBound;
        private final int upperBound;

        public FilterRangeTask(int[] array, int lowerBound, int upperBound) {
            this.array = array;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        protected int[] compute() {
            return Arrays.stream(array)
                    .filter(num -> num >= lowerBound && num <= upperBound)
                    .toArray();
        }
    }

    // злиття мас, залиш елем з 3 мас
    public static int[] mergeArrays(int[] array1, int[] array2, int[] array3) {
        Set<Integer> set1 = Arrays.stream(array1).boxed().collect(Collectors.toSet());
        Set<Integer> set2 = Arrays.stream(array2).boxed().collect(Collectors.toSet());

        return Arrays.stream(array3)
                .filter(num -> !set1.contains(num) && !set2.contains(num))
                .sorted()
                .toArray();
    }
}