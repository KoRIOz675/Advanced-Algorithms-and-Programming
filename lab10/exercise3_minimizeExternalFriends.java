import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class PartitionResult {
    int crossEdges;
    List<Integer> groupA;
    List<Integer> groupB;

    PartitionResult(int crossEdges, List<Integer> groupA, List<Integer> groupB) {
        this.crossEdges = crossEdges;
        this.groupA = groupA;
        this.groupB = groupB;
    }

    public String toString() {
        return "Cross Edges: " + crossEdges + " | Group A: " + groupA + " | Group B: " + groupB;
    }
}

public class exercise3_minimizeExternalFriends {

    public static int countCrossEdges(List<Integer> groupA, List<Integer> groupB, List<List<Integer>> graph) {
        int v = graph.size();
        boolean[] inGroupB = new boolean[v];

        for (int user : groupB) {
            inGroupB[user] = true;
        }

        int count = 0;
        for (int u : groupA) {
            for (int friend : graph.get(u)) {
                if (inGroupB[friend]) {
                    count++;
                }
            }
        }

        return count;
    }

    public static PartitionResult findBalancedPartitionGreedy(List<List<Integer>> graph) {
        int v = graph.size();
        int minSize = (int) Math.ceil(0.4 * v);

        List<Integer> users = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            users.add(i);
        }

        Collections.shuffle(users);
        Random rand = new Random();
        int maxSplit = v - minSize;
        int splitIndex = minSize + (maxSplit == minSize ? 0 : rand.nextInt(maxSplit - minSize + 1));

        boolean[] inGroupA = new boolean[v];
        for (int i = 0; i < splitIndex; i++) {
            inGroupA[users.get(i)] = true;
        }

        int sizeA = splitIndex;
        int sizeB = v - splitIndex;

        List<Integer> initA = new ArrayList<>();
        List<Integer> initB = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            if (inGroupA[i]) initA.add(i);
            else initB.add(i);
        }

        int currentCrossEdges = countCrossEdges(initA, initB, graph);
        boolean improvementFound = true;

        while (improvementFound) {
            improvementFound = false;
            int bestDelta = 0;
            int bestUserToMove = -1;

            for (int i = 0; i < v; i++) {
                if (inGroupA[i] && sizeA - 1 < minSize) continue;
                if (!inGroupA[i] && sizeB - 1 < minSize) continue;

                int friendsInA = 0;
                int friendsInB = 0;

                for (int friend : graph.get(i)) {
                    if (inGroupA[friend]) friendsInA++;
                    else friendsInB++;
                }

                int delta;
                if (inGroupA[i]) {
                    delta = friendsInA - friendsInB;
                } else {
                    delta = friendsInB - friendsInA;
                }

                if (delta < bestDelta) {
                    bestDelta = delta;
                    bestUserToMove = i;
                }
            }

            if (bestDelta < 0) {
                inGroupA[bestUserToMove] = !inGroupA[bestUserToMove];

                if (inGroupA[bestUserToMove]) {
                    sizeA++;
                    sizeB--;
                } else {
                    sizeA--;
                    sizeB++;
                }

                currentCrossEdges += bestDelta;
                improvementFound = true;
            }
        }

        List<Integer> finalA = new ArrayList<>();
        List<Integer> finalB = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            if (inGroupA[i]) finalA.add(i);
            else finalB.add(i);
        }

        return new PartitionResult(currentCrossEdges, finalA, finalB);
    }

    public static PartitionResult findBalancedPartitionLocalSearch(List<List<Integer>> graph, int iterations) {
        PartitionResult bestResult = null;

        for (int i = 0; i < iterations; i++) {
            PartitionResult currentResult = findBalancedPartitionGreedy(graph);

            if (bestResult == null || currentResult.crossEdges < bestResult.crossEdges) {
                bestResult = currentResult;
            }
        }

        return bestResult;
    }

    public static void addEdge(List<List<Integer>> graph, int u, int v) {
        graph.get(u).add(v);
        graph.get(v).add(u);
    }

    public static void main(String[] args) {
        System.out.println("=== TESTING PART 1: COUNT CROSS EDGES ===");
        List<List<Integer>> graph1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) graph1.add(new ArrayList<>());
        addEdge(graph1, 0, 1);
        addEdge(graph1, 1, 2);
        addEdge(graph1, 2, 3);
        addEdge(graph1, 3, 0);
        addEdge(graph1, 0, 2);

        List<Integer> groupA_normal = List.of(0, 1);
        List<Integer> groupB_normal = List.of(2, 3);
        System.out.println("Normal Case - Expected: 3 | Result: " + countCrossEdges(groupA_normal, groupB_normal, graph1));

        List<Integer> groupA_edge = List.of(0, 1, 2, 3);
        List<Integer> groupB_edge = List.of();
        System.out.println("Edge Case (Empty Group) - Expected: 0 | Result: " + countCrossEdges(groupA_edge, groupB_edge, graph1));

        System.out.println("\n=== TESTING PART 2: GREEDY ALGORITHM ===");
        List<List<Integer>> graph2 = new ArrayList<>();
        for (int i = 0; i < 6; i++) graph2.add(new ArrayList<>());
        addEdge(graph2, 0, 1);
        addEdge(graph2, 1, 2);
        addEdge(graph2, 0, 2);
        addEdge(graph2, 3, 4);
        addEdge(graph2, 4, 5);
        addEdge(graph2, 3, 5);
        addEdge(graph2, 2, 3);

        System.out.println("Normal Case (Two clusters connected by 1 edge)");
        System.out.println("Result: " + findBalancedPartitionGreedy(graph2));

        List<List<Integer>> graph3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph3.add(new ArrayList<>());
        addEdge(graph3, 0, 1);
        addEdge(graph3, 0, 2);
        addEdge(graph3, 0, 3);
        addEdge(graph3, 0, 4);
        System.out.println("Edge Case (Star Graph with 5 nodes)");
        System.out.println("Result: " + findBalancedPartitionGreedy(graph3));

        System.out.println("\n=== TESTING PART 3: LOCAL SEARCH ===");
        List<List<Integer>> graph4 = new ArrayList<>();
        for (int i = 0; i < 10; i++) graph4.add(new ArrayList<>());
        addEdge(graph4, 0, 1); addEdge(graph4, 1, 2); addEdge(graph4, 2, 3); addEdge(graph4, 3, 4); addEdge(graph4, 4, 0);
        addEdge(graph4, 5, 6); addEdge(graph4, 6, 7); addEdge(graph4, 7, 8); addEdge(graph4, 8, 9); addEdge(graph4, 9, 5);
        addEdge(graph4, 0, 5); addEdge(graph4, 2, 7);

        System.out.println("Normal Case (Two rings of 5 connected by 2 edges - 100 iterations)");
        System.out.println("Expected Cross Edges: 2");
        System.out.println("Result: " + findBalancedPartitionLocalSearch(graph4, 100));

        List<List<Integer>> graph5 = new ArrayList<>();
        for (int i = 0; i < 5; i++) graph5.add(new ArrayList<>());
        System.out.println("Edge Case (Completely disconnected graph - 10 iterations)");
        System.out.println("Expected Cross Edges: 0");
        System.out.println("Result: " + findBalancedPartitionLocalSearch(graph5, 10));
    }
}