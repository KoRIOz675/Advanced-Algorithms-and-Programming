
import java.util.ArrayList;
import java.util.List;

public class exercise3 {

    // =========================================================
    // RESULT CONTAINER
    // Holds the output of maximize_reach and fast_alternative_strategy
    // =========================================================
    static class KnapsackResult {

        int totalInfluence;
        List<Integer> selectedUsers;

        KnapsackResult(int totalInfluence, List<Integer> selectedUsers) {
            this.totalInfluence = totalInfluence;
            this.selectedUsers = selectedUsers;
        }
    }

    // =========================================================
    // FUNCTION 1: is_within_budget
    // Checks whether the total cost of a selection stays within budget.
    //
    // INPUT:
    //   selection : list of user indices to evaluate
    //   costs     : costs[i] = dollar cost of user i
    //   budget    : maximum total spending allowed
    //
    // OUTPUT:
    //   true  if sum of costs of selected users <= budget
    //   false otherwise
    //
    // COMPLEXITY: O(N)
    // =========================================================
    public static boolean isWithinBudget(List<Integer> selection, int[] costs, int budget) {

        // Step 1: Initialize running total
        int total = 0;

        // Step 2: Sum the cost of each selected user
        for (int i : selection) {
            total += costs[i];
        }

        // Step 3: Return whether total is within budget
        return total <= budget;
    }

    // =========================================================
    // FUNCTION 2: maximize_reach
    // Finds the subset of users maximizing total influence
    // while staying within the budget (0/1 Knapsack via DP).
    //
    // INPUT:
    //   budget     : maximum total spending allowed (capacity W)
    //   costs      : costs[i]     = dollar cost of user i
    //   influences : influences[i] = number of people reached by user i
    //
    // OUTPUT:
    //   KnapsackResult.totalInfluence : maximum influence achievable
    //   KnapsackResult.selectedUsers  : list of user indices (0-based)
    //
    // COMPLEXITY: O(N x budget) time, O(N x budget) space
    // =========================================================
    public static KnapsackResult maximizeReach(int budget, int[] costs, int[] influences) {

        int N = costs.length;

        // Edge case: empty user list or zero budget
        if (N == 0 || budget == 0) {
            return new KnapsackResult(0, new ArrayList<>());
        }

        // Step 1: Initialize DP table dp[0..N][0..budget] with zeros
        // dp[i][w] = best influence using first i users with remaining budget w
        int[][] dp = new int[N + 1][budget + 1];

        // Step 2: Fill table bottom-up, row by row
        for (int i = 1; i <= N; i++) {
            for (int w = 0; w <= budget; w++) {

                // Option 1: skip user i (carry forward previous best)
                dp[i][w] = dp[i - 1][w];

                // Option 2: include user i if affordable
                if (costs[i - 1] <= w) {
                    int candidate = dp[i - 1][w - costs[i - 1]] + influences[i - 1];
                    if (candidate > dp[i][w]) {
                        dp[i][w] = candidate;
                    }
                }
            }
        }

        // Step 3: Read the optimal value
        int maxInfluence = dp[N][budget];

        // Step 4: Reconstruct selected users by backtracking through the table
        List<Integer> selectedUsers = new ArrayList<>();
        int w = budget;
        for (int i = N; i >= 1; i--) {
            // If value changed from row i-1 to i, user i was included
            if (dp[i][w] != dp[i - 1][w]) {
                selectedUsers.add(i - 1);        // convert to 0-based index
                w -= costs[i - 1];               // reduce remaining budget
            }
        }

        // Step 5: Return result
        return new KnapsackResult(maxInfluence, selectedUsers);
    }

    // =========================================================
    // FUNCTION 3: fast_alternative_strategy
    // Greedy approximation: sort users by influence/cost ratio
    // descending and select greedily (no fractions, 0/1 only).
    //
    // INPUT:
    //   budget     : maximum total spending allowed
    //   costs      : costs[i]     = dollar cost of user i
    //   influences : influences[i] = number of people reached by user i
    //
    // OUTPUT:
    //   KnapsackResult.totalInfluence : total influence from greedy selection
    //   KnapsackResult.selectedUsers  : list of user indices chosen
    //
    // COMPLEXITY: O(N log N)
    // =========================================================
    public static KnapsackResult fastAlternativeStrategy(int budget, int[] costs, int[] influences) {

        int N = costs.length;

        // Edge case: empty user list or zero budget
        if (N == 0 || budget == 0) {
            return new KnapsackResult(0, new ArrayList<>());
        }

        // Step 1: Compute influence/cost ratio for each user
        double[] ratios = new double[N];
        for (int i = 0; i < N; i++) {
            ratios[i] = (double) influences[i] / costs[i];
        }

        // Step 2: Build index array and sort by ratio descending
        // Using insertion sort to avoid external libraries
        Integer[] indices = new Integer[N];
        for (int i = 0; i < N; i++) {
            indices[i] = i;
        }

        for (int i = 1; i < N; i++) {
            Integer key = indices[i];
            int j = i - 1;
            while (j >= 0 && ratios[indices[j]] < ratios[key]) {
                indices[j + 1] = indices[j];
                j--;
            }
            indices[j + 1] = key;
        }

        // Step 3: Greedy selection
        int totalInfluence = 0;
        int remainingBudget = budget;
        List<Integer> selectedUsers = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            int idx = indices[i];
            // Take full user only if affordable (no fractions — 0/1 constraint)
            if (costs[idx] <= remainingBudget) {
                selectedUsers.add(idx);
                totalInfluence += influences[idx];
                remainingBudget -= costs[idx];
            }
        }

        // Step 4: Return result
        return new KnapsackResult(totalInfluence, selectedUsers);
    }

    // =========================================================
    // HELPER: print a KnapsackResult in a readable format
    // =========================================================
    private static void printResult(String label, KnapsackResult result) {
        System.out.println(label + " -> influence=" + result.totalInfluence
                + ", users=" + result.selectedUsers);
    }

    // =========================================================
    // MAIN – TESTS
    // =========================================================
    public static void main(String[] args) {

        // =========================================================
        // is_within_budget
        // =========================================================
        System.out.println("=== is_within_budget ===");

        int[] costs1 = {3, 2, 5, 4};
        List<Integer> sel1 = new ArrayList<>();
        sel1.add(1);
        sel1.add(2);   // costs 2 + 5 = 7
        System.out.println("sel={1,2}, budget=7:            " + isWithinBudget(sel1, costs1, 7));   // true

        List<Integer> sel2 = new ArrayList<>();
        sel2.add(0);
        sel2.add(2);   // costs 3 + 5 = 8
        System.out.println("sel={0,2}, budget=7:            " + isWithinBudget(sel2, costs1, 7));   // false

        List<Integer> sel3 = new ArrayList<>();
        sel3.add(0);
        sel3.add(1);   // costs 3 + 2 = 5, exactly on budget
        System.out.println("sel={0,1}, budget=5 (exact):    " + isWithinBudget(sel3, costs1, 5));   // true

        System.out.println("\n=== is_within_budget (edge cases) ===");

        List<Integer> emptySelection = new ArrayList<>();
        System.out.println("empty selection, budget=10:    " + isWithinBudget(emptySelection, costs1, 10)); // true
        System.out.println("empty selection, budget=0:     " + isWithinBudget(emptySelection, costs1, 0));  // true

        List<Integer> singleUser = new ArrayList<>();
        singleUser.add(0);   // cost = 3
        System.out.println("single user cost=3, budget=3:  " + isWithinBudget(singleUser, costs1, 3));  // true
        System.out.println("single user cost=3, budget=2:  " + isWithinBudget(singleUser, costs1, 2));  // false

        List<Integer> allUsers = new ArrayList<>();
        for (int i = 0; i < costs1.length; i++) {
            allUsers.add(i);  // total = 14

                }System.out.println("all users total=14, budget=14: " + isWithinBudget(allUsers, costs1, 14));   // true
        System.out.println("all users total=14, budget=13: " + isWithinBudget(allUsers, costs1, 13));   // false

        // =========================================================
        // maximize_reach – normal cases
        // =========================================================
        System.out.println("\n=== maximize_reach ===");

        // Standard example from the document
        int[] costs2 = {2, 3, 4};
        int[] influences2 = {3, 4, 5};
        KnapsackResult r1 = maximizeReach(5, costs2, influences2);
        printResult("budget=5, costs=[2,3,4], infl=[3,4,5]", r1);  // influence=7, users=[0,1]

        // All users affordable
        int[] costs3 = {1, 2, 3};
        int[] influences3 = {2, 4, 6};
        KnapsackResult r2 = maximizeReach(6, costs3, influences3);
        printResult("budget=6, costs=[1,2,3], infl=[2,4,6]", r2);  // influence=12, users=[0,1,2]

        // Only one user fits
        int[] costs4 = {5, 8, 9};
        int[] influences4 = {10, 15, 20};
        KnapsackResult r3 = maximizeReach(5, costs4, influences4);
        printResult("budget=5, only user 0 fits           ", r3);  // influence=10, users=[0]

        // Counterexample: greedy would pick wrong, DP picks correct
        int[] costs5 = {3, 2, 2};
        int[] influences5 = {4, 3, 3};
        KnapsackResult r4 = maximizeReach(5, costs5, influences5);
        printResult("budget=5, costs=[3,2,2], infl=[4,3,3]", r4);  // influence=7, users=[0,1]

        System.out.println("\n=== maximize_reach (edge cases) ===");

        // Budget = 0: no user can be selected
        KnapsackResult r5 = maximizeReach(0, costs2, influences2);
        printResult("budget=0                             ", r5);  // influence=0, users=[]

        // Empty user list
        KnapsackResult r6 = maximizeReach(100, new int[]{}, new int[]{});
        printResult("N=0 (no users)                       ", r6);  // influence=0, users=[]

        // Single user fits exactly on budget
        KnapsackResult r7 = maximizeReach(3, new int[]{3}, new int[]{7});
        printResult("N=1, budget=3 fits exactly           ", r7);  // influence=7, users=[0]

        // Single user does not fit
        KnapsackResult r8 = maximizeReach(2, new int[]{3}, new int[]{7});
        printResult("N=1, budget=2 too small              ", r8);  // influence=0, users=[]

        // Budget larger than total cost: take every user
        KnapsackResult r9 = maximizeReach(100, new int[]{1, 2, 3}, new int[]{5, 6, 7});
        printResult("budget=100 >> all costs=[1,2,3]      ", r9);  // influence=18, users=[0,1,2]

        // All users have identical cost and influence
        KnapsackResult r10 = maximizeReach(6, new int[]{2, 2, 2, 2}, new int[]{3, 3, 3, 3});
        printResult("all same cost=2 infl=3, budget=6     ", r10); // influence=9, users=[0,1,2]

        // =========================================================
        // fast_alternative_strategy – normal cases
        // =========================================================
        System.out.println("\n=== fast_alternative_strategy ===");

        // Standard example from the document (fractional knapsack style)
        int[] costsG1 = {10, 20, 30};
        int[] influencesG1 = {60, 100, 120};
        KnapsackResult g1 = fastAlternativeStrategy(50, costsG1, influencesG1);
        printResult("budget=50, costs=[10,20,30], infl=[60,100,120]", g1);  // influence=160 (0/1: users 0+1, no fractions)

        // Counterexample: greedy gives 6 while DP gives 7
        int[] costsG2 = {3, 2, 2};
        int[] influencesG2 = {4, 3, 3};
        KnapsackResult g2 = fastAlternativeStrategy(5, costsG2, influencesG2);
        printResult("greedy counterexample budget=5 (expected=6) ", g2);  // influence=6

        // Confirm DP gives the better answer on the same counterexample
        KnapsackResult g2dp = maximizeReach(5, costsG2, influencesG2);
        printResult("DP on same counterexample  (expected=7) ", g2dp);  // influence=7

        System.out.println("\n=== fast_alternative_strategy (edge cases) ===");

        // Budget = 0
        KnapsackResult g3 = fastAlternativeStrategy(0, costsG1, influencesG1);
        printResult("budget=0                             ", g3);  // influence=0, users=[]

        // Empty user list
        KnapsackResult g4 = fastAlternativeStrategy(100, new int[]{}, new int[]{});
        printResult("N=0 (no users)                       ", g4);  // influence=0, users=[]

        // Single user fits exactly
        KnapsackResult g5 = fastAlternativeStrategy(5, new int[]{5}, new int[]{10});
        printResult("N=1, budget=5 fits exactly           ", g5);  // influence=10, users=[0]

        // Single user does not fit
        KnapsackResult g6 = fastAlternativeStrategy(4, new int[]{5}, new int[]{10});
        printResult("N=1, budget=4 too small              ", g6);  // influence=0, users=[]

        // All users have the same ratio: all should be taken if budget allows
        KnapsackResult g7 = fastAlternativeStrategy(12, new int[]{2, 4, 6}, new int[]{4, 8, 12});
        printResult("all ratio=2.0, budget=12             ", g7);  // influence=24, users=[0,1,2]

        // Budget larger than total cost: take all users
        KnapsackResult g8 = fastAlternativeStrategy(100, new int[]{1, 2, 3}, new int[]{5, 6, 7});
        printResult("budget=100 >> all costs=[1,2,3]      ", g8);  // influence=18, users=[0,1,2]
    }
}
