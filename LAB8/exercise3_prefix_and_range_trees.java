import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class exercise3_prefix_and_range_trees {

    static class AutocompleteResult {
        String username;
        Integer userId;

        AutocompleteResult(String username, Integer userId) {
            this.username = username;
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "{" + username + ": " + userId + "}";
        }
    }

    static class TrieNode {
        Map<Character, TrieNode> children;
        boolean is_end_of_username;
        Integer user_id;

        TrieNode() {
            this.children = new HashMap<>();
            this.is_end_of_username = false;
            this.user_id = null;
        }
    }

    static class AutocompleteTrie {
        TrieNode root;
        int total_words;
        int total_nodes;

        AutocompleteTrie() {
            this.root = new TrieNode();
            this.total_words = 0;
            this.total_nodes = 1;
        }

        public void insert(String username, int user_id) {
            TrieNode current = this.root;
            for (char c : username.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    current.children.put(c, new TrieNode());
                    this.total_nodes++;
                }
                current = current.children.get(c);
            }
            if (!current.is_end_of_username) {
                this.total_words++;
            }
            current.is_end_of_username = true;
            current.user_id = user_id;
        }

        public Integer search(String username) {
            TrieNode current = this.root;
            for (char c : username.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return null;
                }
                current = current.children.get(c);
            }
            if (current.is_end_of_username) {
                return current.user_id;
            }
            return null;
        }

        public boolean starts_with(String prefix) {
            TrieNode current = this.root;
            for (char c : prefix.toCharArray()) {
                if (!current.children.containsKey(c)) {
                    return false;
                }
                current = current.children.get(c);
            }
            return true;
        }

        public List<AutocompleteResult> autocomplete(String prefix, int max_results) {
            List<AutocompleteResult> results = new ArrayList<>();
            if (!starts_with(prefix)) {
                return results;
            }

            TrieNode current = this.root;
            for (char c : prefix.toCharArray()) {
                current = current.children.get(c);
            }

            dfs_collect(current, prefix, results, max_results);
            return results;
        }

        private void dfs_collect(TrieNode node, String current_path, List<AutocompleteResult> results, int max_results) {
            if (results.size() >= max_results) {
                return;
            }
            if (node.is_end_of_username) {
                results.add(new AutocompleteResult(current_path, node.user_id));
            }
            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                if (results.size() < max_results) {
                    dfs_collect(entry.getValue(), current_path + entry.getKey(), results, max_results);
                }
            }
        }

        public int count_words() {
            return this.total_words;
        }

        public int get_total_nodes() {
            return this.total_nodes;
        }

        public int get_height() {
            return calculate_height(this.root);
        }

        private int calculate_height(TrieNode node) {
            if (node.children.isEmpty()) {
                return 0;
            }
            int max_child_height = 0;
            for (TrieNode child : node.children.values()) {
                max_child_height = Math.max(max_child_height, calculate_height(child));
            }
            return 1 + max_child_height;
        }

        public void delete(String username) {
            delete_helper(this.root, username, 0);
        }

        private boolean delete_helper(TrieNode node, String username, int depth) {
            if (depth == username.length()) {
                if (!node.is_end_of_username) {
                    return false;
                }
                node.is_end_of_username = false;
                node.user_id = null;
                this.total_words--;
                return node.children.isEmpty();
            }

            char c = username.charAt(depth);
            if (!node.children.containsKey(c)) {
                return false;
            }

            TrieNode child_node = node.children.get(c);
            boolean should_delete_child = delete_helper(child_node, username, depth + 1);

            if (should_delete_child) {
                node.children.remove(c);
                this.total_nodes--;
                return node.children.isEmpty() && !node.is_end_of_username;
            }

            return false;
        }
    }

    static class ActivitySegmentTree {
        int[] tree_sum;
        int[] tree_max;
        int[] tree_min;
        int n;
        int[] leaf_values;

        ActivitySegmentTree(int[] activity_array) {
            this.n = activity_array.length;
            this.leaf_values = activity_array.clone();
            this.tree_sum = new int[4 * this.n];
            this.tree_max = new int[4 * this.n];
            this.tree_min = new int[4 * this.n];
            if (this.n > 0) {
                build_tree(0, 0, this.n - 1);
            }
        }

        private void build_tree(int node, int start, int end) {
            if (start == end) {
                this.tree_sum[node] = this.leaf_values[start];
                this.tree_max[node] = this.leaf_values[start];
                this.tree_min[node] = this.leaf_values[start];
            } else {
                int mid = (start + end) / 2;
                build_tree(2 * node + 1, start, mid);
                build_tree(2 * node + 2, mid + 1, end);
                this.tree_sum[node] = this.tree_sum[2 * node + 1] + this.tree_sum[2 * node + 2];
                this.tree_max[node] = Math.max(this.tree_max[2 * node + 1], this.tree_max[2 * node + 2]);
                this.tree_min[node] = Math.min(this.tree_min[2 * node + 1], this.tree_min[2 * node + 2]);
            }
        }

        public int query(int l, int r) {
            if (this.n == 0 || l > r || l < 0 || r >= this.n) return 0;
            return query_sum(0, 0, this.n - 1, l, r);
        }

        private int query_sum(int node, int start, int end, int l, int r) {
            if (r < start || l > end) {
                return 0;
            }
            if (l <= start && r >= end) {
                return this.tree_sum[node];
            }
            int mid = (start + end) / 2;
            int left_sum = query_sum(2 * node + 1, start, mid, l, r);
            int right_sum = query_sum(2 * node + 2, mid + 1, end, l, r);
            return left_sum + right_sum;
        }

        public int get_range_max(int l, int r) {
            if (this.n == 0 || l > r || l < 0 || r >= this.n) return Integer.MIN_VALUE;
            return query_max(0, 0, this.n - 1, l, r);
        }

        private int query_max(int node, int start, int end, int l, int r) {
            if (r < start || l > end) {
                return Integer.MIN_VALUE;
            }
            if (l <= start && r >= end) {
                return this.tree_max[node];
            }
            int mid = (start + end) / 2;
            int left_max = query_max(2 * node + 1, start, mid, l, r);
            int right_max = query_max(2 * node + 2, mid + 1, end, l, r);
            return Math.max(left_max, right_max);
        }

        public int get_range_min(int l, int r) {
            if (this.n == 0 || l > r || l < 0 || r >= this.n) return Integer.MAX_VALUE;
            return query_min(0, 0, this.n - 1, l, r);
        }

        private int query_min(int node, int start, int end, int l, int r) {
            if (r < start || l > end) {
                return Integer.MAX_VALUE;
            }
            if (l <= start && r >= end) {
                return this.tree_min[node];
            }
            int mid = (start + end) / 2;
            int left_min = query_min(2 * node + 1, start, mid, l, r);
            int right_min = query_min(2 * node + 2, mid + 1, end, l, r);
            return Math.min(left_min, right_min);
        }

        public int get_tree_size() {
            return this.tree_sum.length;
        }

        public int get_height() {
            if (this.n == 0) return 0;
            return (int) Math.ceil(Math.log(this.n) / Math.log(2));
        }

        public int[] get_leaf_values() {
            return this.leaf_values;
        }
    }

    public static void main(String[] args) {
        System.out.println("--- PART A: TRIE TESTS ---");
        AutocompleteTrie trie = new AutocompleteTrie();

        trie.insert("alice", 101);
        trie.insert("alice123", 102);
        trie.insert("bob", 103);
        trie.insert("bobby", 104);
        trie.insert("al", 105);

        System.out.println("REGULAR CASES");
        System.out.println("- Search 'alice': " + trie.search("alice"));
        System.out.println("- Autocomplete 'ali': " + trie.autocomplete("ali", 10));
        System.out.println("- Get Trie Height: " + trie.get_height());
        System.out.println("- Get Total Words: " + trie.count_words());

        System.out.println("EDGE CASES");
        System.out.println("- Autocomplete 'z' (No Match): " + trie.autocomplete("z", 10));
        System.out.println("- Search non-existent 'bobb': " + trie.search("bobb"));

        trie.delete("ghost");
        System.out.println("- Delete non-existent 'ghost'. Total Words: " + trie.count_words());

        trie.delete("alice");
        System.out.println("- Delete 'alice' (prefix of alice123). Search 'alice': " + trie.search("alice"));
        System.out.println("- Search 'alice123' after deleting 'alice': " + trie.search("alice123"));

        trie.delete("bobby");
        System.out.println("- Delete 'bobby'. Search 'bob': " + trie.search("bob"));


        System.out.println("\n--- PART B: SEGMENT TREE TESTS ---");
        int[] activity = {10, 25, 5, 50, 15, 30, 0};
        ActivitySegmentTree st = new ActivitySegmentTree(activity);

        System.out.println("REGULAR CASES");
        System.out.println("- Sum range [1, 4]: " + st.query(1, 4));
        System.out.println("- Get Max range [1, 4]: " + st.get_range_max(1, 4));
        System.out.println("- Get Min range [1, 4]: " + st.get_range_min(1, 4));


        System.out.println("EDGE CASES");
        System.out.println("- Sum single day [3, 3]: " + st.query(3, 3));
        System.out.println("- Query out of bounds [-1, 10]: " + st.query(-1, 10));
        System.out.println("- Max out of bounds [-1, 10]: " + st.get_range_max(-1, 10));

        int[] emptyActivity = {};
        ActivitySegmentTree emptySt = new ActivitySegmentTree(emptyActivity);
        System.out.println("- Empty array sum query [0, 0]: " + emptySt.query(0, 0));
        System.out.println("- Empty array height: " + emptySt.get_height());
    }
}