import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class exercise3_generalizedTrees {

    // ==========================================
    // 1. Data Structures
    // ==========================================

    static class GeneralizedCategoryNode {
        int category_id;
        String name;
        int post_count;
        List<GeneralizedCategoryNode> children;
        GeneralizedCategoryNode parent; // Optional reference to parent

        public GeneralizedCategoryNode(int category_id, String name, int post_count) {
            this.category_id = category_id;
            this.name = name;
            this.post_count = post_count;
            this.children = new ArrayList<>();
            this.parent = null;
        }
    }

    static class BinaryNode {
        int category_id;
        String name;
        int post_count;
        BinaryNode left;  // Represents the FIRST CHILD
        BinaryNode right; // Represents the NEXT SIBLING

        public BinaryNode(int category_id, String name, int post_count) {
            this.category_id = category_id;
            this.name = name;
            this.post_count = post_count;
            this.left = null;
            this.right = null;
        }
    }

    // ==========================================
    // 2. Conversions
    // ==========================================

    public static GeneralizedCategoryNode binary_to_generalized(BinaryNode binary_root) {
        if (binary_root == null) {
            return null;
        }

        GeneralizedCategoryNode gen_root = new GeneralizedCategoryNode(
                binary_root.category_id, binary_root.name, binary_root.post_count
        );

        BinaryNode current_child = binary_root.left;
        while (current_child != null) {
            GeneralizedCategoryNode nary_child = binary_to_generalized(current_child);
            nary_child.parent = gen_root; // Assign parent reference
            gen_root.children.add(nary_child);
            current_child = current_child.right; // Move to the next sibling
        }

        return gen_root;
    }

    public static BinaryNode generalized_to_binary(GeneralizedCategoryNode gen_node) {
        if (gen_node == null) {
            return null;
        }

        BinaryNode b_node = new BinaryNode(
                gen_node.category_id, gen_node.name, gen_node.post_count
        );

        if (!gen_node.children.isEmpty()) {
            b_node.left = generalized_to_binary(gen_node.children.get(0));
            BinaryNode curr_sibling = b_node.left;

            for (int i = 1; i < gen_node.children.size(); i++) {
                BinaryNode next_b_node = generalized_to_binary(gen_node.children.get(i));
                curr_sibling.right = next_b_node;
                curr_sibling = next_b_node;
            }
        }

        return b_node;
    }

    // ==========================================
    // 3. Tree Traversals
    // ==========================================

    public static void pre_order_generalized(GeneralizedCategoryNode node) {
        if (node == null) return;

        System.out.print(node.name + " "); // Process the node

        for (GeneralizedCategoryNode child : node.children) {
            pre_order_generalized(child);
        }
    }

    public static void post_order_generalized(GeneralizedCategoryNode node) {
        if (node == null) return;

        for (GeneralizedCategoryNode child : node.children) {
            post_order_generalized(child);
        }

        System.out.print(node.name + " "); // Process the node
    }

    public static void level_order_generalized(GeneralizedCategoryNode node) {
        if (node == null) return;

        Queue<GeneralizedCategoryNode> Q = new LinkedList<>();
        Q.add(node);

        while (!Q.isEmpty()) {
            GeneralizedCategoryNode current = Q.poll();
            System.out.print(current.name + " "); // Process the node

            for (GeneralizedCategoryNode child : current.children) {
                Q.add(child);
            }
        }
    }

    public static int calculate_fan_out(GeneralizedCategoryNode node) {
        if (node == null || node.children.isEmpty()) return 0;

        int max_fan_out = 0;
        Queue<GeneralizedCategoryNode> Q = new LinkedList<>();
        Q.add(node);

        while (!Q.isEmpty()) {
            GeneralizedCategoryNode current = Q.poll();
            int current_fan_out = current.children.size();

            if (current_fan_out > max_fan_out) {
                max_fan_out = current_fan_out;
            }

            for (GeneralizedCategoryNode child : current.children) {
                Q.add(child);
            }
        }

        return max_fan_out;
    }

    // ==========================================
    // 4. Metrics
    // ==========================================

    public static int calculate_height_generalized(GeneralizedCategoryNode node) {
        if (node == null) return 0;

        int maxHeight = 0;
        for (GeneralizedCategoryNode child : node.children) {
            int childHeight = calculate_height_generalized(child);
            if (childHeight > maxHeight) {
                maxHeight = childHeight;
            }
        }

        return maxHeight + 1;
    }

    public static int count_nodes_generalized(GeneralizedCategoryNode node) {
        if (node == null) return 0;

        int total = 1;
        for (GeneralizedCategoryNode child : node.children) {
            total += count_nodes_generalized(child);
        }

        return total;
    }

    public static int count_leaves_generalized(GeneralizedCategoryNode node) {
        if (node == null) return 0;
        if (node.children.isEmpty()) return 1;

        int leafCount = 0;
        for (GeneralizedCategoryNode child : node.children) {
            leafCount += count_leaves_generalized(child);
        }

        return leafCount;
    }

    public static double count_branching_factor(GeneralizedCategoryNode node) {
        if (node == null) return 0.0;

        int totalCount = 0;
        int leafCount = 0;
        Queue<GeneralizedCategoryNode> Q = new LinkedList<>();
        Q.add(node);

        while (!Q.isEmpty()) {
            GeneralizedCategoryNode current = Q.poll();
            totalCount++;

            if (current.children.isEmpty()) {
                leafCount++;
            } else {
                for (GeneralizedCategoryNode child : current.children) {
                    Q.add(child);
                }
            }
        }

        int nonLeafCount = totalCount - leafCount;
        if (nonLeafCount == 0) return 0.0;

        return (double)(totalCount - 1) / nonLeafCount;
    }

    // ==========================================
    // Testing and Edge Cases
    // ==========================================

    public static void main(String[] args) {
        System.out.println("=== Running Generalized Tree Tests ===\n");

        // ---------------------------------------------------------
        // Test Case 1: Standard Tree Scenario (Mix of depth/breadth)
        // ---------------------------------------------------------
        GeneralizedCategoryNode root = new GeneralizedCategoryNode(1, "Technology", 100);

        GeneralizedCategoryNode prog = new GeneralizedCategoryNode(2, "Programming", 50);
        GeneralizedCategoryNode design = new GeneralizedCategoryNode(3, "Design", 30);
        GeneralizedCategoryNode bus = new GeneralizedCategoryNode(4, "Business", 20);

        GeneralizedCategoryNode py = new GeneralizedCategoryNode(5, "Python", 15);
        GeneralizedCategoryNode java = new GeneralizedCategoryNode(6, "Java", 10);

        root.children.add(prog);
        root.children.add(design);
        root.children.add(bus);

        prog.children.add(py);
        prog.children.add(java);

        System.out.println("--- Test 1: Standard Complex Tree ---");
        System.out.print("Level Order: "); level_order_generalized(root); System.out.println();
        System.out.print("Pre Order: "); pre_order_generalized(root); System.out.println();
        System.out.print("Post Order: "); post_order_generalized(root); System.out.println();

        System.out.println("Total Nodes: " + count_nodes_generalized(root) + " (Expected: 6)");
        System.out.println("Leaf Nodes: " + count_leaves_generalized(root) + " (Expected: 4)");
        System.out.println("Height: " + calculate_height_generalized(root) + " (Expected: 3)");
        System.out.println("Max Fan-out: " + calculate_fan_out(root) + " (Expected: 3)");
        System.out.println("Branching Factor: " + count_branching_factor(root) + " (Expected: 2.5)");

        // Test Conversions
        BinaryNode binaryConverted = generalized_to_binary(root);
        GeneralizedCategoryNode restoredRoot = binary_to_generalized(binaryConverted);
        System.out.print("Restored Tree Level Order: "); level_order_generalized(restoredRoot); System.out.println("\n");

        // ---------------------------------------------------------
        // Test Case 2: Null Edge Case
        // ---------------------------------------------------------
        System.out.println("--- Test 2: Null Tree ---");
        System.out.println("Total Nodes: " + count_nodes_generalized(null) + " (Expected: 0)");
        System.out.println("Branching Factor: " + count_branching_factor(null) + " (Expected: 0.0)\n");

        // ---------------------------------------------------------
        // Test Case 3: Single Node Tree
        // ---------------------------------------------------------
        GeneralizedCategoryNode singleNode = new GeneralizedCategoryNode(1, "OnlyNode", 5);
        System.out.println("--- Test 3: Single Node Tree ---");
        System.out.println("Total Nodes: " + count_nodes_generalized(singleNode) + " (Expected: 1)");
        System.out.println("Leaf Nodes: " + count_leaves_generalized(singleNode) + " (Expected: 1)");
        System.out.println("Height: " + calculate_height_generalized(singleNode) + " (Expected: 1)");
        System.out.println("Max Fan-out: " + calculate_fan_out(singleNode) + " (Expected: 0)");
        System.out.println("Branching Factor: " + count_branching_factor(singleNode) + " (Expected: 0.0)\n");

        // ---------------------------------------------------------
        // Test Case 4: Flat Tree (1 Parent, many children)
        // ---------------------------------------------------------
        GeneralizedCategoryNode flatRoot = new GeneralizedCategoryNode(1, "FlatRoot", 0);
        for (int i = 1; i <= 5; i++) {
            flatRoot.children.add(new GeneralizedCategoryNode(10 + i, "Child" + i, 1));
        }
        System.out.println("--- Test 4: Flat Tree ---");
        System.out.println("Total Nodes: " + count_nodes_generalized(flatRoot) + " (Expected: 6)");
        System.out.println("Height: " + calculate_height_generalized(flatRoot) + " (Expected: 2)");
        System.out.println("Max Fan-out: " + calculate_fan_out(flatRoot) + " (Expected: 5)");
        System.out.println("Branching Factor: " + count_branching_factor(flatRoot) + " (Expected: 5.0)\n");

        // ---------------------------------------------------------
        // Test Case 5: Skewed Tree (Linked-list style, 1 child per node)
        // ---------------------------------------------------------
        GeneralizedCategoryNode skewedRoot = new GeneralizedCategoryNode(1, "Node1", 0);
        GeneralizedCategoryNode temp = skewedRoot;
        for (int i = 2; i <= 4; i++) {
            GeneralizedCategoryNode nextNode = new GeneralizedCategoryNode(i, "Node" + i, 0);
            temp.children.add(nextNode);
            temp = nextNode;
        }
        System.out.println("--- Test 5: Skewed Tree ---");
        System.out.println("Total Nodes: " + count_nodes_generalized(skewedRoot) + " (Expected: 4)");
        System.out.println("Height: " + calculate_height_generalized(skewedRoot) + " (Expected: 4)");
        System.out.println("Max Fan-out: " + calculate_fan_out(skewedRoot) + " (Expected: 1)");
        System.out.println("Branching Factor: " + count_branching_factor(skewedRoot) + " (Expected: 1.0)");
    }
}