import java.util.LinkedList;
import java.util.HashMap;

public class SocialGraph {

    // ─── Structure ────────────────────────────────────────────────────────────

    int n;
    boolean[][] matrix;
    LinkedList<Integer>[] adj_list;
    int num_edges;

    @SuppressWarnings("unchecked")
    public SocialGraph(int n) {
        this.n = n;
        this.matrix = new boolean[n][n];
        this.adj_list = new LinkedList[n];
        for (int i = 0; i < n; i++) {
            adj_list[i] = new LinkedList<Integer>();
        }
        this.num_edges = 0;
    }

    // ─── Add Friendship ───────────────────────────────────────────────────────

    public void addFriendship(int u, int v) {
        // Adjacency Matrix
        matrix[u][v] = true;
        matrix[v][u] = true;

        // Adjacency Linked List
        if (!adj_list[u].contains(v)) {
            adj_list[u].add(v);
        }
        if (!adj_list[v].contains(u)) {
            adj_list[v].add(u);
        }

        num_edges++;
    }

    // ─── Remove Friendship ────────────────────────────────────────────────────

    public void removeFriendship(int u, int v) {
        // Adjacency Matrix
        matrix[u][v] = false;
        matrix[v][u] = false;

        // Adjacency Linked List
        adj_list[u].remove((Integer) v);
        adj_list[v].remove((Integer) u);

        num_edges--;
    }

    // ─── Are Friends (Matrix) ─────────────────────────────────────────────────

    public boolean areFriendsMatrix(int u, int v) {
        if (matrix[u][v] && matrix[v][u]) {
            return true;
        }
        return false;
    }

    // ─── Are Friends (List) ───────────────────────────────────────────────────

    public boolean areFriendsList(int u, int v) {
        for (int element : adj_list[u]) {
            if (element == v) {
                return true;
            }
        }
        return false;
    }

    // ─── Get Friends (Matrix) ─────────────────────────────────────────────────

    public LinkedList<Integer> getFriendsMatrix(int u) {
        LinkedList<Integer> result = new LinkedList<Integer>();
        for (int i = 0; i < n; i++) {
            if (matrix[u][i]) {
                result.add(i);
            }
        }
        return result;
    }

    // ─── Get Friends (List) ───────────────────────────────────────────────────

    public LinkedList<Integer> getFriendsList(int u) {
        return adj_list[u];
    }

    // ─── Get Degree (Matrix) ──────────────────────────────────────────────────

    public int getDegreeMatrix(int u) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (matrix[u][i]) {
                count++;
            }
        }
        return count;
    }

    // ─── Get Degree (List) ────────────────────────────────────────────────────

    public int getDegreeList(int u) {
        return adj_list[u].size();
    }

    // ─── Get Number of Users ──────────────────────────────────────────────────

    public int getNumUsers() {
        return n;
    }

    // ─── Get Number of Edges ──────────────────────────────────────────────────

    public int getNumEdges() {
        return num_edges;
    }

    // ─── Is Complete Graph ────────────────────────────────────────────────────

    public boolean isCompleteGraph() {
        int max_edges = n * (n - 1) / 2;
        if (num_edges == max_edges) {
            return true;
        } else {
            return false;
        }
    }

    // ─── Graph Density ────────────────────────────────────────────────────────

    public double graphDensity() {
        if (n < 2) {
            return 0.0;
        }
        return (2.0 * num_edges) / (n * (n - 1));
    }

    // ─── Degree Distribution ──────────────────────────────────────────────────

    public HashMap<Integer, Integer> degreeDistribution() {
        HashMap<Integer, Integer> dist = new HashMap<Integer, Integer>();
        for (int u = 0; u < n; u++) {
            int d = getDegreeList(u);
            if (!dist.containsKey(d)) {
                dist.put(d, 0);
            }
            dist.put(d, dist.get(d) + 1);
        }
        return dist;
    }

    // ─── Matrix to List ───────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    public LinkedList<Integer>[] matrixToList() {
        LinkedList<Integer>[] list = new LinkedList[n];
        for (int u = 0; u < n; u++) {
            list[u] = new LinkedList<Integer>();
            for (int j = 0; j < n; j++) {
                if (matrix[u][j]) {
                    list[u].add(j);
                }
            }
        }
        return list;
    }

    // ─── List to Matrix ───────────────────────────────────────────────────────

    public boolean[][] listToMatrix() {
        boolean[][] mat = new boolean[n][n];
        for (int u = 0; u < n; u++) {
            for (int v : adj_list[u]) {
                mat[u][v] = true;
            }
        }
        return mat;
    }

    // ─── Main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== addFriendship ===");
        SocialGraph g1 = new SocialGraph(4);
        g1.addFriendship(0, 1);
        g1.addFriendship(0, 2);
        g1.addFriendship(1, 3);
        System.out.println("matrix[0][1]: " + g1.matrix[0][1]);     // true
        System.out.println("matrix[1][0]: " + g1.matrix[1][0]);     // true
        System.out.println("matrix[0][3]: " + g1.matrix[0][3]);     // false
        System.out.println("adj_list[0]:  " + g1.adj_list[0]);      // [1, 2]
        System.out.println("adj_list[1]:  " + g1.adj_list[1]);      // [0, 3]
        System.out.println("num_edges:    " + g1.num_edges);         // 3

        System.out.println("\n=== addFriendship (duplicate, should not double-add) ===");
        g1.addFriendship(0, 1);
        System.out.println("adj_list[0] after duplicate add: " + g1.adj_list[0]); // still [1, 2]

        System.out.println("\n=== removeFriendship ===");
        SocialGraph g2 = new SocialGraph(4);
        g2.addFriendship(0, 1);
        g2.addFriendship(1, 2);
        g2.removeFriendship(0, 1);
        System.out.println("matrix[0][1] after remove: " + g2.matrix[0][1]);  // false
        System.out.println("matrix[1][0] after remove: " + g2.matrix[1][0]);  // false
        System.out.println("adj_list[0] after remove:  " + g2.adj_list[0]);   // []
        System.out.println("adj_list[1] after remove:  " + g2.adj_list[1]);   // [2]
        System.out.println("num_edges after remove:    " + g2.num_edges);      // 1

        System.out.println("\n=== areFriends ===");
        SocialGraph g3 = new SocialGraph(5);
        g3.addFriendship(0, 3);
        System.out.println("areFriendsMatrix(0,3): " + g3.areFriendsMatrix(0, 3));  // true
        System.out.println("areFriendsList(0,3):   " + g3.areFriendsList(0, 3));    // true
        System.out.println("areFriendsMatrix(0,2): " + g3.areFriendsMatrix(0, 2));  // false
        System.out.println("areFriendsList(0,2):   " + g3.areFriendsList(0, 2));    // false
        g3.removeFriendship(0, 3);
        System.out.println("areFriendsMatrix(0,3) after remove: " + g3.areFriendsMatrix(0, 3)); // false
        System.out.println("areFriendsList(0,3)   after remove: " + g3.areFriendsList(0, 3));   // false

        System.out.println("\n=== getFriends ===");
        SocialGraph g4 = new SocialGraph(5);
        g4.addFriendship(2, 0);
        g4.addFriendship(2, 1);
        g4.addFriendship(2, 4);
        System.out.println("getFriendsMatrix(2): " + g4.getFriendsMatrix(2)); // [0, 1, 4]
        System.out.println("getFriendsList(2):   " + g4.getFriendsList(2));   // [0, 1, 4]
        System.out.println("getFriendsMatrix(3) isolated: " + g4.getFriendsMatrix(3)); // []
        System.out.println("getFriendsList(3)   isolated: " + g4.getFriendsList(3));   // []

        System.out.println("\n=== getDegree ===");
        SocialGraph g5 = new SocialGraph(4);
        g5.addFriendship(0, 1);
        g5.addFriendship(0, 2);
        g5.addFriendship(0, 3);
        System.out.println("getDegreeMatrix(0): " + g5.getDegreeMatrix(0)); // 3
        System.out.println("getDegreeList(0):   " + g5.getDegreeList(0));   // 3
        System.out.println("getDegreeMatrix(2): " + g5.getDegreeMatrix(2)); // 1
        System.out.println("getDegreeList(2):   " + g5.getDegreeList(2));   // 1
        System.out.println("getDegreeMatrix(3) isolated: " + g5.getDegreeMatrix(3)); // 1 (connected to 0)

        System.out.println("\n=== getNumUsers / getNumEdges ===");
        SocialGraph g6 = new SocialGraph(6);
        System.out.println("getNumUsers:          " + g6.getNumUsers());  // 6
        System.out.println("getNumEdges (empty):  " + g6.getNumEdges());  // 0
        g6.addFriendship(0, 1);
        g6.addFriendship(2, 3);
        System.out.println("getNumEdges after 2 adds: " + g6.getNumEdges()); // 2
        g6.removeFriendship(0, 1);
        System.out.println("getNumEdges after remove: " + g6.getNumEdges()); // 1

        System.out.println("\n=== isCompleteGraph ===");
        SocialGraph g7 = new SocialGraph(3);
        System.out.println("isComplete (empty 3-node): " + g7.isCompleteGraph()); // false
        g7.addFriendship(0, 1);
        g7.addFriendship(0, 2);
        g7.addFriendship(1, 2);
        System.out.println("isComplete (all edges):    " + g7.isCompleteGraph()); // true
        SocialGraph g7b = new SocialGraph(1);
        System.out.println("isComplete (1 node):       " + g7b.isCompleteGraph()); // true

        System.out.println("\n=== graphDensity ===");
        SocialGraph g8 = new SocialGraph(1);
        System.out.println("density (1 node):          " + g8.graphDensity()); // 0.0
        SocialGraph g8b = new SocialGraph(4);
        System.out.println("density (empty 4-node):    " + g8b.graphDensity()); // 0.0
        g8b.addFriendship(0, 1);
        g8b.addFriendship(0, 2);
        g8b.addFriendship(0, 3);
        System.out.println("density (3 of 6 edges):    " + g8b.graphDensity()); // 0.5
        g8b.addFriendship(1, 2);
        g8b.addFriendship(1, 3);
        g8b.addFriendship(2, 3);
        System.out.println("density (complete 4-node): " + g8b.graphDensity()); // 1.0

        System.out.println("\n=== degreeDistribution ===");
        SocialGraph g9 = new SocialGraph(4);
        g9.addFriendship(0, 1);
        g9.addFriendship(2, 3);
        System.out.println("distribution (all degree-1): " + g9.degreeDistribution()); // {1=4}
        SocialGraph g9b = new SocialGraph(3);
        System.out.println("distribution (all isolated): " + g9b.degreeDistribution()); // {0=3}

        System.out.println("\n=== matrixToList ===");
        SocialGraph g10 = new SocialGraph(3);
        g10.addFriendship(0, 1);
        g10.addFriendship(1, 2);
        LinkedList<Integer>[] converted = g10.matrixToList();
        System.out.println("matrixToList[0]: " + converted[0]); // [1]
        System.out.println("matrixToList[1]: " + converted[1]); // [0, 2]
        System.out.println("matrixToList[2]: " + converted[2]); // [1]

        System.out.println("\n=== listToMatrix ===");
        SocialGraph g11 = new SocialGraph(3);
        g11.addFriendship(0, 2);
        g11.addFriendship(1, 2);
        boolean[][] mat = g11.listToMatrix();
        System.out.println("listToMatrix[0][2]: " + mat[0][2]); // true
        System.out.println("listToMatrix[2][0]: " + mat[2][0]); // true
        System.out.println("listToMatrix[1][2]: " + mat[1][2]); // true
        System.out.println("listToMatrix[0][1]: " + mat[0][1]); // false

        System.out.println("\n=== round-trip (matrixToList then listToMatrix) ===");
        SocialGraph g12 = new SocialGraph(4);
        g12.addFriendship(0, 1);
        g12.addFriendship(1, 3);
        g12.addFriendship(2, 3);
        LinkedList<Integer>[] rtList = g12.matrixToList();
        System.out.println("round-trip list[0]: " + rtList[0]); // [1]
        System.out.println("round-trip list[1]: " + rtList[1]); // [0, 3]
        System.out.println("round-trip list[2]: " + rtList[2]); // [3]
        System.out.println("round-trip list[3]: " + rtList[3]); // [1, 2]
        boolean[][] rtMatrix = g12.listToMatrix();
        System.out.println("round-trip matrix[0][1]: " + rtMatrix[0][1]); // true
        System.out.println("round-trip matrix[1][3]: " + rtMatrix[1][3]); // true
        System.out.println("round-trip matrix[0][2]: " + rtMatrix[0][2]); // false
    }
}
