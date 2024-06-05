import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceOperator {
    private final MergeSort mergeSort;

    public ExecutorServiceOperator(MergeSort mergeSort) {
        this.mergeSort = mergeSort;
    }

    public void sortWithExecutorService(int[] array) {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        executorService.execute(() -> {
            mergeSort.sort(array, 0, array.length - 1);
            executorService.shutdown();
        });
    }
}
