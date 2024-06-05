import java.util.concurrent.RecursiveTask;

public class ForkJoinOperator extends RecursiveTask<int[]> {
    private final int[] array;
    private final int left;
    private final int right;
    private final MergeSort mergeSort;
    private final int threshold;

    public ForkJoinOperator(int[] array, int left, int right, MergeSort mergeSort) {
        this.array = array;
        this.left = left;
        this.right = right;
        this.mergeSort = mergeSort;
        this.threshold = calculateThreshold(array.length);
    }

    private ForkJoinOperator(int[] array, int left, int right, MergeSort mergeSort, int threshold) {
        this.array = array;
        this.left = left;
        this.right = right;
        this.mergeSort = mergeSort;
        this.threshold = threshold;
    }

    private int calculateThreshold(int arrayLength) {
        if (arrayLength == 1000) {
            return 100;
        } else if (arrayLength == 10000) {
            return 1000;
        } else if (arrayLength == 100000) {
            return 10000;
        } else if (arrayLength == 1000000) {
            return 100000;
        } else if (arrayLength == 10000000) {
            return 1000000;
        } else if (arrayLength == 100000000) {
            return 10000000;
        } else {
            return 1000;
        }
    }

    @Override
    protected int[] compute() {
        if (right - left < threshold) {
            mergeSort.sort(array, left, right);
            return array;
        } else {
            int middle = (left + right) / 2;
            ForkJoinOperator leftFork = new ForkJoinOperator(array, left, middle, mergeSort, threshold);
            ForkJoinOperator rightFork = new ForkJoinOperator(array, middle + 1, right, mergeSort, threshold);

            leftFork.fork();
            rightFork.compute();
            leftFork.join();

            mergeSort.merge(array, left, middle, right);
        }
        return array;
    }
}