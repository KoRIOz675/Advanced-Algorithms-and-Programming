
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class exercise2_mutual_friends_detection {

    public static void main(String[] args) {
        Set<Integer> userAFriends = new HashSet<>(Arrays.asList(101, 102, 103, 104, 105));
        Set<Integer> userBFriends = new HashSet<>(Arrays.asList(103, 104, 106, 107, 108));
        System.err.println("User A's friends: " + userAFriends);
        System.err.println("User B's friends: " + userBFriends);
        System.err.println("Mutual friends: " + intersectionFriends(userAFriends, userBFriends)); // Output: [103, 104]
        System.err.println("Friends only in User A's list: " + differenceFriends(userAFriends, userBFriends)); // Output: [101, 102, 105]
        System.err.println("Friends only in User B's list: " + differenceFriends(userBFriends, userAFriends)); // Output: [106, 107, 108]
        System.err.println("All unique friends: " + unionFriends(userAFriends, userBFriends)); // Output: [101, 102, 103, 104, 105, 106, 107, 108]
        System.err.println("Jaccard Similarity: " + jaccardSimilarity(userAFriends, userBFriends)); // Output: 0.25

        System.err.println("\nTesting edge cases:");
        Set<Integer> userCFriends = new HashSet<>(Arrays.asList(101));
        System.err.println("Mutual friends with identical sets: " + intersectionFriends(userAFriends, userAFriends)); // Output: [101, 102, 103, 104, 105]
        System.err.println("Difference with identical sets: " + differenceFriends(userAFriends, userAFriends)); // Output: []
        System.err.println("Union with identical sets: " + unionFriends(userAFriends, userAFriends)); // Output: [101, 102, 103, 104, 105]
        System.err.println("Jaccard Similarity with identical sets: " + jaccardSimilarity(userAFriends, userAFriends)); // Output: 1.0
        System.err.println("Mutual friends with empty set: " + intersectionFriends(userAFriends, new HashSet<>())); // Output: []
        System.err.println("Difference with empty set: " + differenceFriends(userAFriends, new HashSet<>())); // Output: [101, 102, 103, 104, 105]
        System.err.println("Union with empty set: " + unionFriends(userAFriends, new HashSet<>())); // Output: [101, 102, 103, 104, 105]
        System.err.println("Jaccard Similarity with empty set: " + jaccardSimilarity(userAFriends, new HashSet<>())); // Output: 0
        System.err.println("Jaccard Similarity with both sets empty: " + jaccardSimilarity(new HashSet<>(), new HashSet<>())); // Output: 0
        System.err.println("Mutual friends with null set: " + intersectionFriends(userAFriends, null)); // Output: []
        System.err.println("Difference with null set: " + differenceFriends(userAFriends, null)); // Output: []
        System.err.println("Union with null set: " + unionFriends(userAFriends, null)); // Output: []
        System.err.println("Jaccard Similarity with null set: " + jaccardSimilarity(userAFriends, null)); // Output: 0
        System.err.println("Mutual firends with different size sets: " + intersectionFriends(userAFriends, userCFriends)); // Output: [101]
        System.err.println("Difference with different size sets: " + differenceFriends(userAFriends, userCFriends)); // Output: [102, 103, 104, 105]
        System.err.println("Union with different size sets: " + unionFriends(userAFriends, userCFriends)); // Output: [101, 102, 103, 104, 105]
        System.err.println("Jaccard Similarity with different size sets: " + jaccardSimilarity(userAFriends, userCFriends)); // Output: 0.2
    }

    public static Set<Integer> intersectionFriends(Set<Integer> set1, Set<Integer> set2) {
        if (set1 == null || set2 == null) {
            return new HashSet<>();
        }
        Set<Integer> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    public static Set<Integer> differenceFriends(Set<Integer> set1, Set<Integer> set2) {
        if (set1 == null || set2 == null) {
            return new HashSet<>();
        }
        Set<Integer> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    public static Set<Integer> unionFriends(Set<Integer> set1, Set<Integer> set2) {
        if (set1 == null || set2 == null) {
            return new HashSet<>();
        }
        Set<Integer> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    public static float jaccardSimilarity(Set<Integer> set1, Set<Integer> set2) {
        if (set1 == null || set2 == null || set1.isEmpty() && set2.isEmpty()) {
            return 0;
        }
        Set<Integer> intersection = intersectionFriends(set1, set2);
        Set<Integer> union = unionFriends(set1, set2);
        return (float) ((double) intersection.size() / union.size());
    }

}
