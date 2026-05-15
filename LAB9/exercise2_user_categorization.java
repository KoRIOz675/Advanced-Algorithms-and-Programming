import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class exercise2_user_categorization {

    public static class AssignResult {
        public boolean success;
        public int[] labeling;

        public AssignResult(boolean success, int[] labeling) {
            this.success = success;
            this.labeling = labeling;
        }
    }

    public static class MinLabelsResult {
        public int minimum_k;
        public int[] labeling;

        public MinLabelsResult(int minimum_k, int[] labeling) {
            this.minimum_k = minimum_k;
            this.labeling = labeling;
        }
    }

    public static boolean is_valid_labeling(int[] labeling, List<List<Integer>> graph) {
        for (int u = 0; u < graph.size(); u++) {
            for (int v : graph.get(u)) {
                if (u < v) {
                    if (labeling[u] == labeling[v]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static AssignResult assign_labels(int k, List<List<Integer>> graph) {
        int n = graph.size();
        int[] labeling = new int[n];
        Arrays.fill(labeling, -1);

        boolean success = backtrack(0, k, graph, labeling);
        return new AssignResult(success, labeling);
    }

    private static boolean backtrack(int nodeIndex, int k, List<List<Integer>> graph, int[] labeling) {
        if (nodeIndex == graph.size()) {
            return true;
        }

        for (int color = 0; color < k; color++) {
            boolean isValid = true;
            for (int neighbor : graph.get(nodeIndex)) {
                if (labeling[neighbor] == color) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                labeling[nodeIndex] = color;
                if (backtrack(nodeIndex + 1, k, graph, labeling)) {
                    return true;
                }
                labeling[nodeIndex] = -1;
            }
        }
        return false;
    }

    public static MinLabelsResult find_min_labels(List<List<Integer>> graph) {
        if (graph.isEmpty()) {
            return new MinLabelsResult(0, new int[0]);
        }

        int k = 1;
        while (true) {
            AssignResult result = assign_labels(k, graph);
            if (result.success) {
                return new MinLabelsResult(k, result.labeling);
            }
            k++;
        }
    }

    public static List<List<Integer>> buildGraph(int numNodes, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
        return graph;
    }

    public static void main(String[] args) {
        System.out.println("--- TESTING is_valid_labeling ---");

        List<List<Integer>> graph1 = buildGraph(3, new int[][]{{0, 1}, {1, 2}});
        int[] validLabels = {0, 1, 0};
        System.out.println("Normal Case (Valid): " + is_valid_labeling(validLabels, graph1));

        int[] invalidLabels = {0, 1, 1};
        System.out.println("Normal Case (Invalid): " + is_valid_labeling(invalidLabels, graph1));

        List<List<Integer>> emptyGraph = buildGraph(0, new int[][]{});
        System.out.println("Edge Case (Empty Graph): " + is_valid_labeling(new int[]{}, emptyGraph));

        List<List<Integer>> disconnectedGraph = buildGraph(3, new int[][]{});
        System.out.println("Edge Case (No Edges, same labels): " + is_valid_labeling(new int[]{0, 0, 0}, disconnectedGraph));


        System.out.println("\n--- TESTING assign_labels ---");

        List<List<Integer>> lineGraph = buildGraph(4, new int[][]{{0, 1}, {1, 2}, {2, 3}});
        AssignResult resLine1 = assign_labels(1, lineGraph);
        System.out.println("Normal Case (Line Graph, k=1): Success=" + resLine1.success);

        AssignResult resLine2 = assign_labels(2, lineGraph);
        System.out.println("Normal Case (Line Graph, k=2): Success=" + resLine2.success + ", Labels=" + Arrays.toString(resLine2.labeling));

        List<List<Integer>> starGraph = buildGraph(4, new int[][]{{0, 1}, {0, 2}, {0, 3}});
        AssignResult resStar = assign_labels(2, starGraph);
        System.out.println("Normal Case (Star Graph, k=2): Success=" + resStar.success + ", Labels=" + Arrays.toString(resStar.labeling));

        List<List<Integer>> completeGraph = buildGraph(3, new int[][]{{0, 1}, {1, 2}, {0, 2}});
        AssignResult resComplete2 = assign_labels(2, completeGraph);
        System.out.println("Edge Case (Complete Graph, k=2): Success=" + resComplete2.success);

        AssignResult resComplete3 = assign_labels(3, completeGraph);
        System.out.println("Edge Case (Complete Graph, k=3): Success=" + resComplete3.success + ", Labels=" + Arrays.toString(resComplete3.labeling));


        System.out.println("\n--- TESTING find_min_labels ---");

        MinLabelsResult minLine = find_min_labels(lineGraph);
        System.out.println("Normal Case (Line Graph): Min k=" + minLine.minimum_k + ", Labels=" + Arrays.toString(minLine.labeling));

        MinLabelsResult minComplete = find_min_labels(completeGraph);
        System.out.println("Normal Case (Complete Graph N=3): Min k=" + minComplete.minimum_k + ", Labels=" + Arrays.toString(minComplete.labeling));

        MinLabelsResult minDisconnected = find_min_labels(disconnectedGraph);
        System.out.println("Edge Case (Disconnected Graph): Min k=" + minDisconnected.minimum_k + ", Labels=" + Arrays.toString(minDisconnected.labeling));

        MinLabelsResult minEmpty = find_min_labels(emptyGraph);
        System.out.println("Edge Case (Empty Graph): Min k=" + minEmpty.minimum_k + ", Labels=" + Arrays.toString(minEmpty.labeling));
    }
}