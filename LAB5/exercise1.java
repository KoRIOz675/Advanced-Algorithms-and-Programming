import java.util.*;

public class CategoryTree {

    static class CategoryNode {
        String category_id;
        String name;
        int post_count;
        CategoryNode left;
        CategoryNode right;
        CategoryNode parent;

        CategoryNode(String category_id, String name, int post_count) {
            this.category_id = category_id;
            this.name = name;
            this.post_count = post_count;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    static int calculateHeight(CategoryNode node) {
        if (node == null) return 0;
        int left_height = calculateHeight(node.left);
        int right_height = calculateHeight(node.right);
        return 1 + Math.max(left_height, right_height);
    }

    static int calculateNodeHeight(CategoryNode node, String target_id, int current_depth) {
        if (node == null) return -1;
        if (node.category_id.equals(target_id)) return current_depth;
        int left_result = calculateNodeHeight(node.left, target_id, current_depth + 1);
        if (left_result != -1) return left_result;
        return calculateNodeHeight(node.right, target_id, current_depth + 1);
    }

    static int countNodes(CategoryNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    static int countLeaves(CategoryNode node) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }

    static boolean isBalanced(CategoryNode node) {
        if (node == null) return true;
        int left_height = calculateHeight(node.left);
        int right_height = calculateHeight(node.right);
        int diff = Math.abs(left_height - right_height);
        return diff <= 1 && isBalanced(node.left) && isBalanced(node.right);
    }

    static boolean isFullBinaryTree(CategoryNode node) {
        if (node == null) return true;
        if (node.left == null && node.right == null) return true;
        if (node.left != null && node.right != null) return isFullBinaryTree(node.left) && isFullBinaryTree(node.right);
        return false;
    }

    static boolean isPerfectBinaryTree(CategoryNode node) {
        int h = calculateHeight(node);
        int expected = (int) Math.pow(2, h) - 1;
        return countNodes(node) == expected;
    }

    static boolean isCompleteBinaryTree(CategoryNode node) {
        if (node == null) return true;
        Queue<CategoryNode> queue = new LinkedList<>();
        queue.add(node);
        boolean found_null = false;
        while (!queue.isEmpty()) {
            CategoryNode current = queue.poll();
            if (current.left == null) {
                found_null = true;
            } else {
                if (found_null) return false;
                queue.add(current.left);
            }
            if (current.right == null) {
                found_null = true;
            } else {
                if (found_null) return false;
                queue.add(current.right);
            }
        }
        return true;
    }

    static CategoryNode findCategory(CategoryNode node, String category_id) {
        if (node == null) return null;
        if (node.category_id.equals(category_id)) return node;
        CategoryNode left_result = findCategory(node.left, category_id);
        if (left_result != null) return left_result;
        return findCategory(node.right, category_id);
    }

    static List<String> findPathToRoot(CategoryNode root, String category_id) {
        CategoryNode target = findCategory(root, category_id);
        if (target == null) return new ArrayList<>();
        List<String> path = new ArrayList<>();
        CategoryNode current = target;
        while (current != null) {
            path.add(current.name);
            current = current.parent;
        }
        return path;
    }

    static CategoryNode lowestCommonAncestor(CategoryNode node, String id1, String id2) {
        if (node == null) return null;
        if (node.category_id.equals(id1) || node.category_id.equals(id2)) return node;
        CategoryNode left_lca = lowestCommonAncestor(node.left, id1, id2);
        CategoryNode right_lca = lowestCommonAncestor(node.right, id1, id2);
        if (left_lca != null && right_lca != null) return node;
        if (left_lca != null) return left_lca;
        return right_lca;
    }

    static CategoryNode link(CategoryNode parent, CategoryNode left, CategoryNode right) {
        if (left != null) {
            parent.left = left;
            left.parent = parent;
        }
        if (right != null) {
            parent.right = right;
            right.parent = parent;
        }
        return parent;
    }

    public static void main(String[] args) {

        CategoryNode A = new CategoryNode("A", "tech", 120);
        CategoryNode B = new CategoryNode("B", "mobile", 80);
        CategoryNode C = new CategoryNode("C", "backend", 60);
        CategoryNode D = new CategoryNode("D", "ios", 40);
        CategoryNode E = new CategoryNode("E", "android", 35);
        CategoryNode F = new CategoryNode("F", "db", 25);

        link(A, B, C);
        link(B, D, E);
        link(C, null, F);
        CategoryNode root = A;

        // ── calculateHeight ──────────────────────────────────────────────
        System.out.println("=== calculateHeight ===");
        System.out.println(calculateHeight(root));       // expected: 3
        System.out.println(calculateHeight(null));       // expected: 0 (empty tree)
        System.out.println(calculateHeight(D));          // expected: 1 (leaf)

        // ── calculateNodeHeight ───────────────────────────────────────────
        System.out.println("\n=== calculateNodeHeight ===");
        System.out.println(calculateNodeHeight(root, "A", 0)); // expected: 0 (root itself)
        System.out.println(calculateNodeHeight(root, "D", 0)); // expected: 2
        System.out.println(calculateNodeHeight(root, "F", 0)); // expected: 2
        System.out.println(calculateNodeHeight(root, "Z", 0)); // expected: -1 (not found)

        // ── countNodes ───────────────────────────────────────────────────
        System.out.println("\n=== countNodes ===");
        System.out.println(countNodes(root));   // expected: 6
        System.out.println(countNodes(null));   // expected: 0 (empty tree)
        System.out.println(countNodes(D));      // expected: 1 (single leaf)

        // ── countLeaves ──────────────────────────────────────────────────
        System.out.println("\n=== countLeaves ===");
        System.out.println(countLeaves(root));  // expected: 3 (D, E, F)
        System.out.println(countLeaves(null));  // expected: 0 (empty tree)
        System.out.println(countLeaves(D));     // expected: 1 (D is a leaf)

        // ── isBalanced ───────────────────────────────────────────────────
        System.out.println("\n=== isBalanced ===");
        System.out.println(isBalanced(root));   // expected: true
        System.out.println(isBalanced(null));   // expected: true (empty is balanced)

        CategoryNode unbalanced = new CategoryNode("X", "x", 0);
        unbalanced.left = new CategoryNode("Y", "y", 0);
        unbalanced.left.parent = unbalanced;
        unbalanced.left.left = new CategoryNode("Z", "z", 0);
        unbalanced.left.left.parent = unbalanced.left;
        System.out.println(isBalanced(unbalanced)); // expected: false (left depth 2, right 0)

        // ── isFullBinaryTree ─────────────────────────────────────────────
        System.out.println("\n=== isFullBinaryTree ===");
        System.out.println(isFullBinaryTree(root));  // expected: false (C has only right child)
        System.out.println(isFullBinaryTree(B));     // expected: true (B has both D and E)
        System.out.println(isFullBinaryTree(null));  // expected: true

        CategoryNode fullRoot = new CategoryNode("R", "r", 0);
        link(fullRoot, new CategoryNode("L", "l", 0), new CategoryNode("Ri", "ri", 0));
        System.out.println(isFullBinaryTree(fullRoot)); // expected: true

        // ── isPerfectBinaryTree ───────────────────────────────────────────
        System.out.println("\n=== isPerfectBinaryTree ===");
        CategoryNode p1 = new CategoryNode("P1", "p1", 0);
        CategoryNode p2 = new CategoryNode("P2", "p2", 0);
        CategoryNode p3 = new CategoryNode("P3", "p3", 0);
        CategoryNode p4 = new CategoryNode("P4", "p4", 0);
        CategoryNode p5 = new CategoryNode("P5", "p5", 0);
        CategoryNode p6 = new CategoryNode("P6", "p6", 0);
        CategoryNode p7 = new CategoryNode("P7", "p7", 0);
        link(p1, p2, p3);
        link(p2, p4, p5);
        link(p3, p6, p7);
        System.out.println(isPerfectBinaryTree(p1));   // expected: true  (7 nodes, height 3)
        System.out.println(isPerfectBinaryTree(root)); // expected: false (6 nodes)
        System.out.println(isPerfectBinaryTree(null)); // expected: true  (empty: 0 nodes == 2^0-1)

        // ── isCompleteBinaryTree ──────────────────────────────────────────
        System.out.println("\n=== isCompleteBinaryTree ===");
        System.out.println(isCompleteBinaryTree(p1));   // expected: true  (perfect is also complete)
        System.out.println(isCompleteBinaryTree(root)); // expected: false (C has no left child but has right)
        System.out.println(isCompleteBinaryTree(null)); // expected: true

        CategoryNode cr = new CategoryNode("CR", "cr", 0);
        CategoryNode cl = new CategoryNode("CL", "cl", 0);
        CategoryNode crr = new CategoryNode("CRR", "crr", 0);
        link(cr, cl, crr);
        System.out.println(isCompleteBinaryTree(cr));   // expected: true

        // ── findCategory ─────────────────────────────────────────────────
        System.out.println("\n=== findCategory ===");
        CategoryNode found = findCategory(root, "F");
        System.out.println(found != null ? found.name : "null"); // expected: db
        System.out.println(findCategory(root, "Z"));             // expected: null
        System.out.println(findCategory(null, "A"));             // expected: null

        // ── findPathToRoot ────────────────────────────────────────────────
        System.out.println("\n=== findPathToRoot ===");
        System.out.println(findPathToRoot(root, "F")); // expected: [db, backend, tech]
        System.out.println(findPathToRoot(root, "A")); // expected: [tech]
        System.out.println(findPathToRoot(root, "Z")); // expected: [] (not found)

        // ── lowestCommonAncestor ──────────────────────────────────────────
        System.out.println("\n=== lowestCommonAncestor ===");
        CategoryNode lca1 = lowestCommonAncestor(root, "D", "E");
        System.out.println(lca1 != null ? lca1.name : "null"); // expected: mobile

        CategoryNode lca2 = lowestCommonAncestor(root, "D", "F");
        System.out.println(lca2 != null ? lca2.name : "null"); // expected: tech

        CategoryNode lca3 = lowestCommonAncestor(root, "A", "F");
        System.out.println(lca3 != null ? lca3.name : "null"); // expected: tech (root is ancestor)

        CategoryNode lca4 = lowestCommonAncestor(root, "Z", "W");
        System.out.println(lca4);                              // expected: null (neither exists)
    }
}