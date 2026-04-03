import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

class CommentNode {
    int comment_id;
    int user_id;
    String content;
    LocalDateTime timestamp;
    int likes;
    List<CommentNode> replies;

    public CommentNode(int comment_id, int user_id, String content, int likes) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.content = content;
        this.likes = likes;
        this.timestamp = LocalDateTime.now();
        this.replies = new ArrayList<>();
    }
}

public class exercise1_threadTraversal {

    public static String indentation(int level) {
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < level * 2; i++) {
            space.append(" ");
        }
        return space.toString();
    }

    public static void display_thread(CommentNode comment, int level) {
        if (comment == null) return;
        System.out.println(indentation(level) + "User " + comment.user_id + ": " + comment.content);
        for (CommentNode reply : comment.replies) {
            display_thread(reply, level + 1);
        }
    }

    public static int count_total_comments(CommentNode comment) {
        if (comment == null) return 0;
        int total = 1;
        for (CommentNode reply : comment.replies) {
            total = total + count_total_comments(reply);
        }
        return total;
    }

    public static int total_likes(CommentNode comment) {
        if (comment == null) return 0;
        int total = comment.likes;
        for (CommentNode reply : comment.replies) {
            total = total + total_likes(reply);
        }
        return total;
    }

    public static int find_deepest_reply(CommentNode comment) {
        if (comment == null || comment.replies.isEmpty()) {
            return 0;
        }
        int max_depth = 0;
        for (CommentNode reply : comment.replies) {
            int current_depth = find_deepest_reply(reply);
            if (current_depth > max_depth) {
                max_depth = current_depth;
            }
        }
        return max_depth + 1;
    }

    public static List<CommentNode> search_by_user(int user_id, CommentNode comment) {
        List<CommentNode> comments = new ArrayList<>();
        if (comment == null) return comments;

        if (user_id == comment.user_id) {
            comments.add(comment);
        }
        for (CommentNode reply : comment.replies) {
            comments.addAll(search_by_user(user_id, reply));
        }
        return comments;
    }

    public static List<CommentNode> contains_keyword(String keyword, CommentNode comment) {
        List<CommentNode> matching_comments = new ArrayList<>();
        if (comment == null || keyword == null || keyword.isEmpty()) return matching_comments;

        if (comment.content.toLowerCase().contains(keyword.toLowerCase())) {
            matching_comments.add(comment);
        }
        for (CommentNode reply : comment.replies) {
            matching_comments.addAll(contains_keyword(keyword, reply));
        }
        return matching_comments;
    }

    public static CommentNode delete_comment(int comment_id, CommentNode current_comment) {
        if (current_comment == null) return null;

        if (current_comment.comment_id == comment_id) {
            return null;
        }

        List<CommentNode> filtered_replies = new ArrayList<>();
        for (CommentNode reply : current_comment.replies) {
            CommentNode result = delete_comment(comment_id, reply);
            if (result != null) {
                filtered_replies.add(result);
            }
        }
        current_comment.replies = filtered_replies;
        return current_comment;
    }

    // --- TEST SUITE ---
    public static void main(String[] args) {
        System.out.println("========== RUNNING TESTS ==========\n");

        // ---------------------------------------------------------
        // TEST CASE 1: Single Node (Edge Case - No replies)
        // ---------------------------------------------------------
        System.out.println("--- TEST 1: Single Node (Edge Case) ---");
        CommentNode singleNode = new CommentNode(1, 999, "Just me here.", 5);
        display_thread(singleNode, 0);
        System.out.println("Total Comments (Expected 1): " + count_total_comments(singleNode));
        System.out.println("Max Depth (Expected 0): " + find_deepest_reply(singleNode));
        System.out.println();

        // ---------------------------------------------------------
        // TEST CASE 2: Deep Linear Thread (Edge Case - A replies to B replies to C)
        // ---------------------------------------------------------
        System.out.println("--- TEST 2: Deep Linear Thread ---");
        CommentNode linearRoot = new CommentNode(10, 101, "Level 0", 1);
        CommentNode l1 = new CommentNode(11, 102, "Level 1", 2);
        CommentNode l2 = new CommentNode(12, 103, "Level 2", 3);
        CommentNode l3 = new CommentNode(13, 104, "Level 3", 4);

        linearRoot.replies.add(l1);
        l1.replies.add(l2);
        l2.replies.add(l3);

        display_thread(linearRoot, 0);
        System.out.println("Max Depth (Expected 3): " + find_deepest_reply(linearRoot));
        System.out.println("Total Likes (Expected 10): " + total_likes(linearRoot));
        System.out.println();

        // ---------------------------------------------------------
        // TEST CASE 3: Generic Complex Thread (Happy Path)
        // ---------------------------------------------------------
        System.out.println("--- TEST 3: Generic Complex Thread ---");
        CommentNode root = new CommentNode(100, 1, "Root comment about recursion", 10);
        CommentNode r1 = new CommentNode(101, 2, "I love recursion", 5);
        CommentNode r2 = new CommentNode(102, 3, "I prefer iteration", 2);
        CommentNode r1_1 = new CommentNode(103, 1, "Recursion is elegant", 8);
        CommentNode r1_2 = new CommentNode(104, 4, "But watch out for stack overflow!", 15);
        CommentNode r1_1_1 = new CommentNode(105, 2, "True, base cases are important.", 3);

        root.replies.add(r1);
        root.replies.add(r2);
        r1.replies.add(r1_1);
        r1.replies.add(r1_2);
        r1_1.replies.add(r1_1_1);

        display_thread(root, 0);
        System.out.println("Total Comments (Expected 6): " + count_total_comments(root));
        System.out.println();

        // ---------------------------------------------------------
        // TEST CASE 4: Search Edge Cases
        // ---------------------------------------------------------
        System.out.println("--- TEST 4: Search Edge Cases ---");
        System.out.println("Search for User 999 (Non-existent, Expected 0 matches): " + search_by_user(999, root).size());
        System.out.println("Search for User 1 (Expected 2 matches): " + search_by_user(1, root).size());

        System.out.println("Search keyword 'RECURSION' (Case insensitive, Expected 3 matches): " + contains_keyword("RECURSION", root).size());
        System.out.println("Search keyword 'pizza' (Non-existent, Expected 0 matches): " + contains_keyword("pizza", root).size());
        System.out.println();

        // ---------------------------------------------------------
        // TEST CASE 5: Deletion Edge Cases
        // ---------------------------------------------------------
        System.out.println("--- TEST 5: Deletion Edge Cases ---");

        // Deleting a non-existent comment
        root = delete_comment(999, root);
        System.out.println("After deleting ID 999 (Non-existent, Expected 6 comments): " + count_total_comments(root));

        // Deleting a leaf node (no cascade needed)
        root = delete_comment(105, root);
        System.out.println("After deleting ID 105 (Leaf, Expected 5 comments): " + count_total_comments(root));

        // Deleting a middle node (Cascade triggers)
        root = delete_comment(101, root);
        System.out.println("After deleting ID 101 (Middle cascade, Expected 2 comments left): " + count_total_comments(root));
        display_thread(root, 0);

        // Deleting the root node itself
        root = delete_comment(100, root);
        System.out.println("After deleting root ID 100 (Expected 0 comments): " + count_total_comments(root));
    }
}