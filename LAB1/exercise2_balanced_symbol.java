import java.util.List;
import java.util.Stack;

public class exercise2_balanced_symbol {

    public static void main(String[] args) {
        System.out.println(isBalanced("["));                    // Output false
        System.out.println(isBalanced("{}[[()]]"));             // Output true
        System.out.println(isBalanced("([)]"));                 // Output false
        System.out.println(isBalanced(""));                     // Output true

    }

    // Exercise 2
    public static boolean isBalanced(String s) {
        List<Character> opening = List.of('{', '(', '[');
        List<Character> closing = List.of('}', ')', ']');
        int comparisons = 0;


        Stack<Character> pile = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (opening.contains(c)) {
                pile.push(c);
            } else if (closing.contains(c)) {
                comparisons++;
                if (pile.isEmpty()) {
                    return false;
                }
                Character compare = pile.pop();
                if (opening.indexOf(compare) != closing.indexOf(c)) {
                    return false;
                }
            }
        }
        System.out.println("Comparisons : " + comparisons);
        return pile.isEmpty();
    }

}