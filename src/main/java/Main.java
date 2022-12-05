import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int threadAmount = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
        int average = 0;
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            FirstTest secondTest = new FirstTest(executorService);
            secondTest.calculate("file/");
            long finish = System.currentTimeMillis();
            long result = finish - start;
            average += result;
        }
        executorService.shutdown();
        System.out.println("average: " + (average / runs));
        System.out.println("total: " + (average));
        System.out.println("Thread amount: " + threadAmount);
    }
}
