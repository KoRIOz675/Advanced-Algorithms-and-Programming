

public class exercise1_integer_mirror {

    public static void main(String[] args) {
        System.out.println(invert(123));            // Output 321
        System.out.println(invert(4000));           // Output 4
        System.out.println(invert(2));              // Output 2
        System.out.println(invert(0));              // Output 0
        System.out.println(invert(694));            // Output 496

    }

    // Exercise 1
    public static int invert(int n) {
        if (n < 10) {
            return n;
        }
        int res = 0;
        while (n != 0) {
            int digit = n % 10;
            res = res * 10 + digit;
            n = n / 10;
        }
        return res;
    }

}