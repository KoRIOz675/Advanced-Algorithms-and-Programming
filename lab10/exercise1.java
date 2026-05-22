
import java.util.*;

public class exercise1 {

    public static class MaxSetResult {
        int size;
        List<Integer> members;

        MaxSetResult(int size, List<Integer> members) {
            this.size = size;
            this.members = members;
        }
    }

    public static boolean isValidInvitation(List<Integer> invited, Map<Integer, List<Integer>> graph) {
        Set<Integer> invitedSet = new HashSet<>(invited);
        for (int u : invited) {
            for (int v : graph.getOrDefault(u, Collections.emptyList())) {
                if (invitedSet.contains(v)) {
                    return false;
                }
            }
        }
        return true;
    }

    static int bestSize;
    static List<Integer> bestSet;

    public static MaxSetResult findMaxInvitationsExact(Map<Integer, List<Integer>> graph) {
        List<Integer> nodes = new ArrayList<>(graph.keySet());
        bestSize = 0;
        bestSet = new ArrayList<>();
        backtrack(nodes, new ArrayList<>(), graph);
        return new MaxSetResult(bestSize, new ArrayList<>(bestSet));
    }

    private static void backtrack(List<Integer> candidates, List<Integer> current, Map<Integer, List<Integer>> graph) {
        if (current.size() + candidates.size() <= bestSize) {
            return;
        }
        if (candidates.isEmpty()) {
            if (current.size() > bestSize) {
                bestSize = current.size();
                bestSet = new ArrayList<>(current);
            }
            return;
        }
        int u = candidates.get(0);
        List<Integer> remaining = new ArrayList<>(candidates.subList(1, candidates.size()));

        Set<Integer> neighborsOfU = new HashSet<>(graph.getOrDefault(u, Collections.emptyList()));
        List<Integer> newCandidates = new ArrayList<>();
        for (int v : remaining) {
            if (!neighborsOfU.contains(v)) {
                newCandidates.add(v);
            }
        }

        List<Integer> newCurrent = new ArrayList<>(current);
        newCurrent.add(u);
        backtrack(newCandidates, newCurrent, graph);
        backtrack(remaining, current, graph);
    }

    public static MaxSetResult findMaxInvitationsGreedy(Map<Integer, List<Integer>> graph) {
        Set<Integer> available = new HashSet<>(graph.keySet());
        List<Integer> invited = new ArrayList<>();

        while (!available.isEmpty()) {
            int u = -1;
            int minDegree = Integer.MAX_VALUE;
            for (int node : available) {
                int degree = 0;
                for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                    if (available.contains(neighbor)) {
                        degree++;
                    }
                }
                if (degree < minDegree) {
                    minDegree = degree;
                    u = node;
                }
            }
            invited.add(u);
            Set<Integer> toRemove = new HashSet<>();
            toRemove.add(u);
            for (int neighbor : graph.getOrDefault(u, Collections.emptyList())) {
                if (available.contains(neighbor)) {
                    toRemove.add(neighbor);
                }
            }
            available.removeAll(toRemove);
        }

        return new MaxSetResult(invited.size(), invited);
    }

    private static Map<Integer, List<Integer>> buildGraph(int n, int[][] edges) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
        return graph;
    }

    private static void printResult(String label, MaxSetResult result) {
        System.out.println(label + " -> size=" + result.size + ", members=" + result.members);
    }

    public static void main(String[] args) {
        System.out.println("=== is_valid_invitation ===");

        Map<Integer, List<Integer>> g1 = buildGraph(4, new int[][]{{0, 1}, {1, 2}, {2, 3}});

        System.out.println("invited={0,2}, path 0-1-2-3:      " + isValidInvitation(Arrays.asList(0, 2), g1));        // true
        System.out.println("invited={0,1}, path 0-1-2-3:      " + isValidInvitation(Arrays.asList(0, 1), g1));        // false
        System.out.println("invited={0,2,3}, path 0-1-2-3:    " + isValidInvitation(Arrays.asList(0, 2, 3), g1));     // false (edge 2-3)
        System.out.println("empty invited:                     " + isValidInvitation(new ArrayList<>(), g1));          // true
        System.out.println("single user {1}:                   " + isValidInvitation(Arrays.asList(1), g1));           // true
        System.out.println("invited={0,3}, no edge between:    " + isValidInvitation(Arrays.asList(0, 3), g1));        // true

        System.out.println("\n=== find_max_invitations_exact ===");

        Map<Integer, List<Integer>> g2 = buildGraph(4, new int[][]{{0, 1}, {1, 2}, {2, 3}});
        printResult("path 0-1-2-3 (expected size=2)     ", findMaxInvitationsExact(g2));

        Map<Integer, List<Integer>> g3 = buildGraph(4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}});
        printResult("4-cycle (expected size=2)           ", findMaxInvitationsExact(g3));

        Map<Integer, List<Integer>> g4 = buildGraph(3, new int[][]{{0, 1}, {1, 2}, {0, 2}});
        printResult("triangle (expected size=1)          ", findMaxInvitationsExact(g4));

        Map<Integer, List<Integer>> g5 = buildGraph(4, new int[][]{{0, 1}, {1, 2}});
        printResult("path+isolated node 3 (expected=3)  ", findMaxInvitationsExact(g5));

        Map<Integer, List<Integer>> g6 = buildGraph(6, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 5}});
        printResult("6-cycle (expected size=3)           ", findMaxInvitationsExact(g6));

        Map<Integer, List<Integer>> g7 = buildGraph(5, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 0}});
        printResult("5-cycle (expected size=2)           ", findMaxInvitationsExact(g7));

        System.out.println("\n=== find_max_invitations_greedy ===");

        printResult("path 0-1-2-3 (expected size=2)     ", findMaxInvitationsGreedy(g2));
        printResult("4-cycle (expected size=2)           ", findMaxInvitationsGreedy(g3));
        printResult("triangle (expected size=1)          ", findMaxInvitationsGreedy(g4));
        printResult("path+isolated node 3 (expected=3)  ", findMaxInvitationsGreedy(g5));
        printResult("6-cycle (expected size=3)           ", findMaxInvitationsGreedy(g6));
        printResult("5-cycle (expected size=2)           ", findMaxInvitationsGreedy(g7));

        System.out.println("\n=== greedy vs exact comparison ===");

        Map<Integer, List<Integer>> gc = buildGraph(7, new int[][]{{0, 1}, {0, 2}, {1, 3}, {2, 3}, {3, 4}, {4, 5}, {4, 6}, {5, 6}});
        MaxSetResult exact = findMaxInvitationsExact(gc);
        MaxSetResult greedy = findMaxInvitationsGreedy(gc);
        printResult("7-node graph exact                 ", exact);
        printResult("7-node graph greedy                ", greedy);
    }
}
