
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class exercise6_first_unique {

    public static void main(String[] args) {
        System.err.println(firstUniqueTwoPass("leetcode"));             // Output: 0
        System.err.println(firstUniqueTwoPass("loveleetcode"));         // Output: 2
        System.err.println(firstUniqueTwoPass("aabb"));                 // Output: -1
        System.err.println(firstUniqueTwoPass("dddccdbba"));            // Output: 8

        System.err.println(firstUniqueOrderedDic("leetcode"));          // Output: 0
        System.err.println(firstUniqueOrderedDic("loveleetcode"));      // Output: 2
        System.err.println(firstUniqueOrderedDic("aabb"));              // Output: -1
        System.err.println(firstUniqueOrderedDic("dddccdbba"));         // Output: 8
    }

    // Using two passes with frequency dictionary
    public static int firstUniqueTwoPass(String s) {
        if (s == null || s.length() == 0) {
            return -1;
        }

        Map<Character, Integer> frequency = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            frequency.put(ch, frequency.getOrDefault(ch, 0) + 1);
        }

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (frequency.get(ch) == 1) {
                return i;
            }
        }

        return -1;
    }

    // Using ordered dictionary or linked hash map
    public static int firstUniqueOrderedDic(String s) {
        if (s == null || s.length() == 0) {
            return -1;
        }

        LinkedHashMap<Character, int[]> map = new LinkedHashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            int[] entry = map.get(ch);
            if (entry == null) {
                map.put(ch, new int[]{1, i});
            } else {
                entry[0] += 1;
            }
        }

        for (Map.Entry<Character, int[]> element : map.entrySet()) {
            int[] value = element.getValue();
            if (value[0] == 1) {
                return value[1];
            }
        }

        return -1;
    }
}
