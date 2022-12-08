import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) throws NoSuchFieldException {
//        int threadAmount = 1;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadAmount);
//        int average = 0;
//        int runs = 1000;
//        for (int i = 0; i < runs; i++) {
//            long start = System.currentTimeMillis();
//            FirstTest firstTest = new FirstTest(executorService);
//            firstTest.calculate("file/");
//            long finish = System.currentTimeMillis();
//            long result = finish - start;
//            average += result;
//        }
//        executorService.shutdown();
//        System.out.println("average: " + (average / runs));
//        System.out.println("total: " + (average));
//        System.out.println("Thread amount: " + threadAmount);

        File file = new File("test.properties");
        Path path = Path.of(file.getPath());

        Properties o = SecondTest.loadFromProperties(Properties.class, path);
        System.out.println(o);
    }
}
