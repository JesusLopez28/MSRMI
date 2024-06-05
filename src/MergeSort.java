import javax.swing.*;

public class MergeSort {

    private JTextArea originalTextArea;

    public MergeSort( ) {
    }

    public void sort(int[] array, int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            sort(array, left, middle);
            sort(array, middle + 1, right);
            merge(array, left, middle, right);
        }
    }

    public void merge(int[] array, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i)
            L[i] = array[left + i];
        for (int j = 0; j < n2; ++j)
            R[j] = array[middle + 1 + j];

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
        }
    }

    public int[] generateRandomArray(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = (int) (Math.random() * 100);
            originalTextArea.append(array[i] + " ");
        }

        return array;
    }

    public String printArray(int[] array) {
        StringBuilder result = new StringBuilder();
        for (int j : array) {
            result.append(j).append(" ");
        }
        return result.toString();
    }

    // Set y get de originalTextArea
    public JTextArea getOriginalTextArea() {
        return originalTextArea;
    }

    public void setOriginalTextArea(JTextArea originalTextArea) {
        this.originalTextArea = originalTextArea;
    }

}
