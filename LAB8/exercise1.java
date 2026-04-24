    import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBST {

    // =========================================================
    // STRUCTURE: UserNode
    // =========================================================
    static class UserNode {
        int user_id;
        String name;
        int[] friends;
        UserNode leftChild;
        UserNode rightChild;

        UserNode(int user_id, String name, int[] friends) {
            this.user_id = user_id;
            this.name = name;
            this.friends = friends;
            this.leftChild = null;
            this.rightChild = null;
        }
    }

    // =========================================================
    // BST root
    // =========================================================
    private UserNode root;

    public UserBST() {
        this.root = null;
    }

    // =========================================================
    // INSERT
    // =========================================================
    public void insert(int user_id, String name, int[] friends_list) {
        UserNode tempUserNode = new UserNode(user_id, name, friends_list);

        if (root == null) {
            root = tempUserNode;
            return;
        }

        insertCompare(tempUserNode, root);
    }

    /**
     * Recursive helper that navigates the tree and inserts tempUserNode
     * at the correct position relative to currentNode.
     * Returns the (possibly updated) currentNode so the parent can re-link.
     */
    private UserNode insertCompare(UserNode tempUserNode, UserNode currentNode) {
        if (tempUserNode.user_id < currentNode.user_id) {
            if (currentNode.leftChild == null) {
                currentNode.leftChild = tempUserNode;
            } else if (currentNode.leftChild.user_id == tempUserNode.user_id) {
                return currentNode; // duplicate – do nothing
            } else {
                insertCompare(tempUserNode, currentNode.leftChild);
            }
        } else if (tempUserNode.user_id > currentNode.user_id) {
            if (currentNode.rightChild == null) {
                currentNode.rightChild = tempUserNode;
            } else if (currentNode.rightChild.user_id == tempUserNode.user_id) {
                return currentNode; // duplicate – do nothing
            } else {
                insertCompare(tempUserNode, currentNode.rightChild);
            }
        }
        // equal to currentNode itself → do nothing
        return currentNode;
    }

    // =========================================================
    // FIND
    // =========================================================
    public UserNode find(int user_id) {
        if (root == null) return null;
        return findCompare(user_id, root);
    }

    private UserNode findCompare(int user_id, UserNode currentNode) {
        if (user_id < currentNode.user_id) {
            currentNode = currentNode.leftChild;
            if (currentNode == null) return null;
            return findCompare(user_id, currentNode);
        } else if (user_id > currentNode.user_id) {
            currentNode = currentNode.rightChild;
            if (currentNode == null) return null;
            return findCompare(user_id, currentNode);
        } else {
            return currentNode;
        }
    }

    // =========================================================
    // INORDER TRAVERSAL
    // =========================================================
    public List<Integer> inorderTraversal() {
        List<Integer> resultList = new ArrayList<>();
        inorderHelper(root, resultList);
        return resultList;
    }

    private void inorderHelper(UserNode userNode, List<Integer> resultList) {
        if (userNode == null) return;
        inorderHelper(userNode.leftChild, resultList);
        resultList.add(userNode.user_id);
        inorderHelper(userNode.rightChild, resultList);
    }

    // =========================================================
    // DELETE
    // =========================================================
    public void delete(int user_id) {
        if (find(user_id) == null) return; // user does not exist
        root = deleteHelper(root, user_id);
    }

    private UserNode deleteHelper(UserNode userNode, int user_id) {
        if (userNode == null) return null;

        if (user_id < userNode.user_id) {
            userNode.leftChild = deleteHelper(userNode.leftChild, user_id);
        } else if (user_id > userNode.user_id) {
            userNode.rightChild = deleteHelper(userNode.rightChild, user_id);
        } else {
            // Found the node to delete
            // Case: leaf
            if (userNode.leftChild == null && userNode.rightChild == null) {
                return null;
            }
            // Case: only right child
            if (userNode.leftChild == null) {
                return userNode.rightChild;
            }
            // Case: only left child
            if (userNode.rightChild == null) {
                return userNode.leftChild;
            }
            // Case: two children → replace with right child, keep left child
            // Attach the left subtree to the leftmost node of the right subtree
            UserNode rightChild = userNode.rightChild;
            UserNode leftMost = rightChild;
            while (leftMost.leftChild != null) {
                leftMost = leftMost.leftChild;
            }
            leftMost.leftChild = userNode.leftChild;
            return rightChild;
        }
        return userNode;
    }

    // =========================================================
    // SUGGEST FRIENDS
    // =========================================================
    public List<Integer> suggestFriends(int user_id, int max_suggestions) {
        UserNode userNode = find(user_id);
        if (userNode == null) return new ArrayList<>();

        // Build a quick lookup set for direct friends and self
        boolean[] isDirectFriend = new boolean[0]; // We'll use a HashMap instead
        Map<Integer, Boolean> directFriends = new HashMap<>();
        directFriends.put(user_id, true);
        if (userNode.friends != null) {
            for (int f : userNode.friends) {
                directFriends.put(f, true);
            }
        }

        // Count appearances of each friend-of-friend
        Map<Integer, Integer> countMap = new HashMap<>();
        suggestHelper(userNode, directFriends, countMap);

        // Sort entries by count descending
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(countMap.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        // Build result list up to max_suggestions
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < entries.size() && result.size() < max_suggestions; i++) {
            result.add(entries.get(i).getKey());
        }
        return result;
    }

    // Overload with default max_suggestions = 5
    public List<Integer> suggestFriends(int user_id) {
        return suggestFriends(user_id, 5);
    }

    private void suggestHelper(UserNode userNode, Map<Integer, Boolean> directFriends,
                                Map<Integer, Integer> resultSet) {
        if (userNode.friends == null) return;
        for (int friendId : userNode.friends) {
            UserNode tempNode = find(friendId);
            if (tempNode == null || tempNode.friends == null) continue;
            for (int friend1 : tempNode.friends) {
                if (tempNode.user_id != userNode.user_id) {
                    if (!directFriends.containsKey(friend1)) {
                        resultSet.put(friend1, resultSet.getOrDefault(friend1, 0) + 1);
                    }
                }
            }
        }
    }

    // =========================================================
    // GET HEIGHT
    // =========================================================
    public int getHeight() {
        if (root == null) return 0;
        return heightHelper(root);
    }

    private int heightHelper(UserNode userNode) {
        if (userNode == null) return 0;
        int leftHeight = heightHelper(userNode.leftChild);
        int rightHeight = heightHelper(userNode.rightChild);
        return 1 + Math.max(leftHeight, rightHeight);
    }

    // =========================================================
    // IS BALANCED
    // =========================================================
    public boolean isBalanced() {
        return isBalancedHelper(root);
    }

    private boolean isBalancedHelper(UserNode userNode) {
        if (userNode == null) return true;
        int leftHeight = heightHelper(userNode.leftChild);
        int rightHeight = heightHelper(userNode.rightChild);
        if (Math.abs(leftHeight - rightHeight) > 1) return false;
        return isBalancedHelper(userNode.leftChild) && isBalancedHelper(userNode.rightChild);
    }

    // =========================================================
    // GET LEAF COUNT
    // =========================================================
    public int getLeafCount() {
        if (root == null) return 0;
        return leafHelper(root, 0);
    }

    private int leafHelper(UserNode userNode, int count) {
        if (userNode == null) return count;
        if (userNode.leftChild == null && userNode.rightChild == null) {
            return count + 1;
        }
        return leafHelper(userNode.leftChild, count) + leafHelper(userNode.rightChild, count);
    }

    // =========================================================
    // MAIN – TESTS
    // =========================================================
    public static void main(String[] args) {

        // =========================================================
        // 1. insert / find
        // =========================================================
        System.out.println("=== insert / find ===");
        UserBST bst = new UserBST();
        bst.insert(10, "Alice",   new int[]{20, 30});
        bst.insert(5,  "Bob",     new int[]{10, 15});
        bst.insert(15, "Charlie", new int[]{10, 20});
        bst.insert(3,  "Dave",    new int[]{5});
        bst.insert(7,  "Eve",     new int[]{5, 10});
        bst.insert(12, "Frank",   new int[]{15});
        bst.insert(20, "Grace",   new int[]{10, 15});
        System.out.println("find(10).name:  " + bst.find(10).name);        // Alice
        System.out.println("find(5).name:   " + bst.find(5).name);         // Bob
        System.out.println("find(15).name:  " + bst.find(15).name);        // Charlie
        System.out.println("find(3).name:   " + bst.find(3).name);         // Dave
        System.out.println("find(99):       " + bst.find(99));             // null
        System.out.println("find(1) empty:  " + new UserBST().find(1));    // null

        System.out.println("\n=== insert (duplicate, should keep original) ===");
        bst.insert(10, "Duplicate", new int[]{});
        System.out.println("find(10).name after dup insert: " + bst.find(10).name); // Alice

        System.out.println("\n=== insert / find (edge cases) ===");
        System.out.println("find(-1) before insert: " + bst.find(-1));              // null
        System.out.println("find(0)  before insert: " + bst.find(0));               // null
        bst.insert(-5, "Negative", new int[]{});
        System.out.println("find(-5).name:          " + bst.find(-5).name);         // Negative
        bst.insert(0, "Zero", new int[]{});
        System.out.println("find(0).name:           " + bst.find(0).name);          // Zero
        bst.insert(Integer.MAX_VALUE, "MaxInt", new int[]{});
        System.out.println("find(MAX_VALUE) != null: " + (bst.find(Integer.MAX_VALUE) != null)); // true
        bst.insert(50, "NullFriends", null);
        System.out.println("find(50) null-friends:   " + bst.find(50).name);        // NullFriends

        System.out.println("\n=== inorder_traversal ===");
        UserBST inBst = new UserBST();
        inBst.insert(10, "Alice",   new int[]{});
        inBst.insert(5,  "Bob",     new int[]{});
        inBst.insert(15, "Charlie", new int[]{});
        inBst.insert(3,  "Dave",    new int[]{});
        inBst.insert(7,  "Eve",     new int[]{});
        inBst.insert(12, "Frank",   new int[]{});
        inBst.insert(20, "Grace",   new int[]{});
        System.out.println("inorderTraversal():        " + inBst.inorderTraversal());   // [3, 5, 7, 10, 12, 15, 20]
        UserBST emptyBst = new UserBST();
        System.out.println("inorderTraversal (empty):  " + emptyBst.inorderTraversal()); // []
        UserBST singleBst = new UserBST();
        singleBst.insert(42, "Solo", new int[]{});
        System.out.println("inorderTraversal (single): " + singleBst.inorderTraversal()); // [42]

        System.out.println("\n=== inorder_traversal (ascending insert, right-skewed) ===");
        UserBST ascBst = new UserBST();
        for (int v : new int[]{1, 2, 3, 4, 5}) ascBst.insert(v, "U" + v, new int[]{});
        System.out.println("inorderTraversal (1→5): " + ascBst.inorderTraversal()); // [1, 2, 3, 4, 5]

        System.out.println("\n=== delete (leaf) ===");
        inBst.delete(3);
        System.out.println("find(3) after delete:   " + inBst.find(3));            // null
        System.out.println("inorderTraversal after: " + inBst.inorderTraversal()); // [5, 7, 10, 12, 15, 20]

        System.out.println("\n=== delete (node with one right child) ===");
        inBst.delete(12);
        inBst.delete(15);
        System.out.println("find(15) after delete:     " + inBst.find(15));         // null
        System.out.println("find(20) still exists:     " + inBst.find(20).name);    // Grace

        System.out.println("\n=== delete (root with two children) ===");
        UserBST bst2 = new UserBST();
        bst2.insert(10, "Alice",   new int[]{});
        bst2.insert(5,  "Bob",     new int[]{});
        bst2.insert(15, "Charlie", new int[]{});
        bst2.insert(3,  "Dave",    new int[]{});
        bst2.insert(7,  "Eve",     new int[]{});
        bst2.insert(12, "Frank",   new int[]{});
        bst2.insert(20, "Grace",   new int[]{});
        bst2.delete(10);
        System.out.println("find(10) after root delete: " + bst2.find(10));              // null
        System.out.println("find(5)  still exists:      " + bst2.find(5).name);          // Bob
        System.out.println("find(15) still exists:      " + bst2.find(15).name);         // Charlie
        System.out.println("inorderTraversal after:     " + bst2.inorderTraversal());    // [3, 5, 7, 12, 15, 20]

        System.out.println("\n=== delete (edge cases) ===");
        inBst.delete(999);
        System.out.println("delete(999) non-existing: no crash");
        emptyBst.delete(1);
        System.out.println("delete(1) on empty tree:  no crash");
        UserBST oneBst = new UserBST();
        oneBst.insert(7, "Only", new int[]{});
        oneBst.delete(7);
        System.out.println("find(7) after sole-root delete:     " + oneBst.find(7));              // null
        System.out.println("inorderTraversal after sole delete: " + oneBst.inorderTraversal());   // []
        System.out.println("getHeight after sole delete:        " + oneBst.getHeight());          // 0
        oneBst.insert(7, "Reborn", new int[]{});
        System.out.println("find(7).name after re-insert:       " + oneBst.find(7).name);        // Reborn

        System.out.println("\n=== delete (node with only left child) ===");
        UserBST leftChildBst = new UserBST();
        leftChildBst.insert(10, "Root", new int[]{});
        leftChildBst.insert(5,  "Left", new int[]{});
        leftChildBst.insert(3,  "LL",   new int[]{});
        leftChildBst.delete(5);
        System.out.println("find(5) after delete:    " + leftChildBst.find(5));             // null
        System.out.println("find(3) still reachable: " + leftChildBst.find(3).name);        // LL
        System.out.println("inorderTraversal after:  " + leftChildBst.inorderTraversal());  // [3, 10]

        System.out.println("\n=== get_height ===");
        System.out.println("getHeight (empty):             " + emptyBst.getHeight());   // 0
        System.out.println("getHeight (single):            " + singleBst.getHeight());  // 1
        UserBST heightBst = new UserBST();
        heightBst.insert(10, "A", new int[]{});
        heightBst.insert(5,  "B", new int[]{});
        heightBst.insert(15, "C", new int[]{});
        heightBst.insert(3,  "D", new int[]{});
        System.out.println("getHeight (3-level tree):      " + heightBst.getHeight());  // 3
        UserBST skewed = new UserBST();
        for (int i = 1; i <= 5; i++) skewed.insert(i, "N" + i, new int[]{});
        System.out.println("getHeight (right-skewed, n=5): " + skewed.getHeight());     // 5
        UserBST leftSkewed = new UserBST();
        for (int i = 5; i >= 1; i--) leftSkewed.insert(i, "L" + i, new int[]{});
        System.out.println("getHeight (left-skewed,  n=5): " + leftSkewed.getHeight()); // 5

        System.out.println("\n=== get_height (after deletion) ===");
        UserBST shrinkBst = new UserBST();
        shrinkBst.insert(10, "A", new int[]{});
        shrinkBst.insert(5,  "B", new int[]{});
        shrinkBst.insert(3,  "C", new int[]{});
        System.out.println("getHeight before delete: " + shrinkBst.getHeight()); // 3
        shrinkBst.delete(3);
        System.out.println("getHeight after delete:  " + shrinkBst.getHeight()); // 2

        System.out.println("\n=== is_balanced ===");
        System.out.println("isBalanced (empty):              " + emptyBst.isBalanced());   // true
        System.out.println("isBalanced (single):             " + singleBst.isBalanced());  // true
        System.out.println("isBalanced (heightBst):          " + heightBst.isBalanced());  // true
        System.out.println("isBalanced (right-skewed):       " + skewed.isBalanced());     // false
        UserBST perfectBst = new UserBST();
        perfectBst.insert(8,  "A", new int[]{});
        perfectBst.insert(4,  "B", new int[]{});
        perfectBst.insert(12, "C", new int[]{});
        perfectBst.insert(2,  "D", new int[]{});
        perfectBst.insert(6,  "E", new int[]{});
        perfectBst.insert(10, "F", new int[]{});
        perfectBst.insert(14, "G", new int[]{});
        System.out.println("isBalanced (perfect 7-node):     " + perfectBst.isBalanced()); // true
        perfectBst.insert(1, "H", new int[]{});
        perfectBst.insert(0, "I", new int[]{});
        System.out.println("isBalanced (2 extra left levels):" + perfectBst.isBalanced()); // false

        System.out.println("\n=== get_leaf_count ===");
        System.out.println("getLeafCount (empty):                      " + emptyBst.getLeafCount());   // 0
        System.out.println("getLeafCount (single):                     " + singleBst.getLeafCount());  // 1
        System.out.println("getLeafCount (heightBst, leaves=3&15):     " + heightBst.getLeafCount());  // 2
        UserBST fullBst = new UserBST();
        fullBst.insert(10, "A", new int[]{});
        fullBst.insert(5,  "B", new int[]{});
        fullBst.insert(15, "C", new int[]{});
        fullBst.insert(3,  "D", new int[]{});
        fullBst.insert(7,  "E", new int[]{});
        fullBst.insert(12, "F", new int[]{});
        fullBst.insert(20, "G", new int[]{});
        System.out.println("getLeafCount (full 7-node, leaves=3,7,12,20): " + fullBst.getLeafCount()); // 4
        fullBst.insert(1, "H", new int[]{});
        System.out.println("getLeafCount after adding child to 3:         " + fullBst.getLeafCount()); // 4
        fullBst.delete(1);
        System.out.println("getLeafCount after deleting that new leaf:    " + fullBst.getLeafCount()); // 4

        System.out.println("\n=== suggest_friends ===");
        UserBST social = new UserBST();
        social.insert(1, "User1", new int[]{2, 3});
        social.insert(2, "User2", new int[]{1, 4, 5});
        social.insert(3, "User3", new int[]{1, 5, 6});
        social.insert(4, "User4", new int[]{2, 7});
        social.insert(5, "User5", new int[]{2, 3});
        social.insert(6, "User6", new int[]{3});
        social.insert(7, "User7", new int[]{4});
        // User 1 friends: {2,3}. FOF via 2: {4,5}, via 3: {5,6} → 5 scores 2
        System.out.println("suggestFriends(1, 5):              " + social.suggestFriends(1, 5));               // [5, ...]
        System.out.println("suggestFriends(1, 5).get(0) == 5:  " + (social.suggestFriends(1, 5).get(0) == 5)); // true

        System.out.println("\n=== suggest_friends (edge cases) ===");
        System.out.println("suggestFriends(999, 5):            " + social.suggestFriends(999, 5));  // []
        social.insert(99, "Loner", new int[]{});
        System.out.println("suggestFriends(99, no friends):    " + social.suggestFriends(99, 5));   // []
        social.insert(88, "NullF", null);
        System.out.println("suggestFriends(88, null friends):  " + social.suggestFriends(88, 5));   // []
        System.out.println("suggestFriends(1, 0):              " + social.suggestFriends(1, 0));    // []
        System.out.println("suggestFriends(1, 1):              " + social.suggestFriends(1, 1));    // [5]
        System.out.println("suggestFriends(1) default max:     " + social.suggestFriends(1));       // [5, ...]

        System.out.println("\n=== suggest_friends (closed group, no new suggestions) ===");
        social.insert(10, "AllKnown", new int[]{11, 12});
        social.insert(11, "Friend1",  new int[]{10, 12});
        social.insert(12, "Friend2",  new int[]{10, 11});
        System.out.println("suggestFriends(10) closed group:   " + social.suggestFriends(10, 5));  // []
    }
}
