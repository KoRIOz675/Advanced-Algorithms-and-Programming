
import java.util.Arrays;

public class exercise5_array_rotation {

    public static void main(String[] args) {
        System.err.println(Arrays.toString(rotationTempArray(new int[]{1, 2, 3, 4, 5, 6, 7}, 10))); // Output: [5, 6, 7, 1, 2, 3, 4]
        System.err.println(Arrays.toString(rotationOneByOne(new int[]{1, 2, 3, 4, 5, 6, 7}, 10)));  // Output: [5, 6, 7, 1, 2, 3, 4]
        System.err.println(Arrays.toString(rotationReverse(new int[]{1, 2, 3, 4, 5, 6, 7}, 10)));   // Output: [5, 6, 7, 1, 2, 3, 4]

        System.err.println("Testing edge cases:");
        System.err.println(Arrays.toString(rotationTempArray(new int[]{1}, 1)));    // Output: [1]
        System.err.println(Arrays.toString(rotationOneByOne(new int[]{1}, 1)));     // Output: [1]
        System.err.println(Arrays.toString(rotationReverse(new int[]{1}, 1)));      // Output: [1]
        System.err.println(Arrays.toString(rotationTempArray(new int[]{}, 3)));    // Output: []
        System.err.println(Arrays.toString(rotationOneByOne(new int[]{}, 3)));     // Output: []
        System.err.println(Arrays.toString(rotationReverse(new int[]{}, 3)));      // Output: []
        System.err.println(Arrays.toString(rotationTempArray(new int[]{1, 2, 3}, -1)));   // Output: [2, 3, 1]
        System.err.println(Arrays.toString(rotationOneByOne(new int[]{1, 2, 3}, -1)));    // Output: [2, 3, 1]
        System.err.println(Arrays.toString(rotationReverse(new int[]{1, 2, 3}, -1)));     // Output: [2, 3, 1]
        System.err.println(Arrays.toString(rotationTempArray(new int[]{1, 2, 3}, 0)));    // Output: [1, 2, 3]
        System.err.println(Arrays.toString(rotationOneByOne(new int[]{1, 2, 3}, 0)));     // Output: [1, 2, 3]
        System.err.println(Arrays.toString(rotationReverse(new int[]{1, 2, 3}, 0)));      // Output: [1, 2, 3]

    }

    public static int[] rotationTempArray(int[] arr, int k) {

        if (arr == null || arr.length == 0) {
            return arr;
        }
        int length = arr.length;
        k = k % length;
        if (k < 0) {
            k += length;
        }
        int[] tempArr = new int[length];
        for (int i = 0; i < length; i++) {
            tempArr[(i + k) % length] = arr[i];
        }
        System.arraycopy(tempArr, 0, arr, 0, length);
        return arr;
    }

    private static int[] rotationOneByOne(int[] arr, int k) {
        if (arr == null || arr.length == 0) {
            return arr;
        }
        int length = arr.length;
        k = k % length;
        if (k < 0) {
            k += length;
        }
        for (int i = 0; i < k; i++) {
            int lastIndex = arr[length - 1];
            for (int j = length - 1; j > 0; j--) {
                arr[j] = arr[j - 1];
            }
            arr[0] = lastIndex;
        }
        return arr;
    }

    private static int[] rotationReverse(int[] arr, int k) {
        if (arr == null || arr.length == 0) {
            return arr;
        }
        int length = arr.length;
        k = k % length;
        if (k < 0) {
            k += length;
        }
        reverseHelper(arr, 0, length - 1);
        reverseHelper(arr, 0, k - 1);
        reverseHelper(arr, k, length - 1);
        return arr;
    }

    public static int[] reverseHelper(int[] arr, int start, int end) {
        if (arr == null) {
            return arr;
        }
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
        return arr;
    }

}
