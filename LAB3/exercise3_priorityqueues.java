import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class exercise3_priorityqueues {
    public static void main(String[] args) {
        System.out.println("--- Testing Sorted Linked List Priority Queue (Ex 3) ---");
        SortedLinkedListPQ pq = new SortedLinkedListPQ();

        Post p1 = new Post(1, 101, "Morning coffee", 1000, 10, 5, 2);
        Post p2 = new Post(2, 102, "Workout complete", 2000, 50, 10, 5);
        Post p3 = new Post(3, 103, "Sunset photo", 3000, 100, 20, 10);

        pq.enqueue(p1);
        pq.enqueue(p2);
        pq.enqueue(p3);

        System.out.print("Initial Queue (Should be P3 -> P2 -> P1): ");
        pq.display();

        System.out.println("\n--- Testing updateScore (Question 2) ---");
        System.out.println("Updating Post 1 with massive engagement...");
        pq.updateScore(1, 200, 50, 20);
        System.out.print("After Update (P1 should be at front): ");
        pq.display();

        System.out.println("\n--- Testing decayOlderThan ---");
        System.out.println("Decaying posts older than timestamp 2500 by 50%...");
        pq.decayOlderThan(2500, 0.50);
        System.out.print("After Decay (P1: 180, P3: 170, P2: 42): ");
        pq.display();

        System.out.println("\n--- Testing getTopK ---");
        System.out.println("Top 2 posts: " + pq.getTopK(2));

        System.out.println("\n--- Demonstration for Question 5 (Max-Heap) ---");
        PriorityQueue<Post> nativeHeapPQ = new PriorityQueue<>();
        nativeHeapPQ.add(p1);
        nativeHeapPQ.add(p2);
        nativeHeapPQ.add(p3);

        System.out.println("Native Heap Polling (O(log n)): " + nativeHeapPQ.poll());
    }
}

class Post implements Comparable<Post> {
    int postId;
    int userId;
    String content;
    long timestamp;
    int likes;
    int comments;
    int shares;
    int engagementScore;

    public Post(int postId, int userId, String content, long timestamp, int likes, int comments, int shares) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        calculateScore();
    }

    public void calculateScore() {
        this.engagementScore = (this.likes * 1) + (this.comments * 2) + (this.shares * 3);
    }

    @Override
    public int compareTo(Post other) {
        // Descending order for native Java PriorityQueue
        return Integer.compare(other.engagementScore, this.engagementScore);
    }

    @Override
    public String toString() {
        return String.format("[Post %d: Score %d]", postId, engagementScore);
    }
}

// ---------------------------------------------------------
// Creating the PriorityQueue as a SortedLinkedList
// ---------------------------------------------------------
class SortedLinkedListPQ {
    private class Node {
        Post post;
        Node next;

        Node(Post post) {
            this.post = post;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public SortedLinkedListPQ() {
        this.head = null;
        this.size = 0;
    }

    // Enqueue: O(n)
    public void enqueue(Post post) {
        Node newNode = new Node(post);

        if (head == null || post.engagementScore > head.post.engagementScore) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.post.engagementScore >= post.engagementScore) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    // Dequeue Max: O(1)
    public Post dequeueMax() {
        if (isEmpty()) return null;
        Post maxPost = head.post;
        head = head.next;
        size--;
        return maxPost;
    }

    public Post peekMax() {
        if (isEmpty()) return null;
        return head.post;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    // Update Score: O(n)
    public void updateScore(int postId, int newLikes, int newComments, int newShares) {
        Node current = head;
        Node prev = null;

        while (current != null && current.post.postId != postId) {
            prev = current;
            current = current.next;
        }

        if (current == null) return;

        if (prev == null) {
            head = current.next;
        } else {
            prev.next = current.next;
        }
        size--;

        current.post.likes = newLikes;
        current.post.comments = newComments;
        current.post.shares = newShares;
        current.post.calculateScore();

        enqueue(current.post);
    }

    // Refresh All: O(n^2)
    public void refreshAll() {
        Node current = head;
        head = null;
        size = 0;

        while (current != null) {
            current.post.calculateScore();
            Node nextNode = current.next;
            enqueue(current.post);
            current = nextNode;
        }
    }

    // Get Top K: O(k)
    public List<Post> getTopK(int k) {
        List<Post> result = new ArrayList<>();
        Node current = head;
        int count = 0;
        while (current != null && count < k) {
            result.add(current.post);
            current = current.next;
            count++;
        }
        return result;
    }

    // Decay older than: O(n^2)
    public void decayOlderThan(long timestamp, double decayFactor) {
        Node current = head;
        head = null;
        size = 0;

        while (current != null) {
            Node nextNode = current.next;
            if (current.post.timestamp < timestamp) {
                current.post.engagementScore = (int)(current.post.engagementScore * (1.0 - decayFactor));
            }
            enqueue(current.post);
            current = nextNode;
        }
    }

    public void display() {
        Node current = head;
        while (current != null) {
            System.out.print(current.post + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }
}
