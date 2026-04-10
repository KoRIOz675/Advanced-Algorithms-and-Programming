import java.util.*;
import java.util.stream.Collectors;

public class exercise3_bfs {

    // --------------------------------------------------------
    // GRAPH STRUCTURE (Adjacency List Representation)
    // --------------------------------------------------------
    private Map<String, List<String>> graph;

    public exercise3_bfs() {
        this.graph = new HashMap<>();
    }

    public void addUser(String user) {
        graph.putIfAbsent(user, new ArrayList<>());
    }

    public void addFriendship(String user1, String user2) {
        addUser(user1);
        addUser(user2);
        // Unoriented Graph: friendship goes both ways!
        if (!graph.get(user1).contains(user2)) graph.get(user1).add(user2);
        if (!graph.get(user2).contains(user1)) graph.get(user2).add(user1);
    }

    public List<String> getFriends(String user) {
        return graph.getOrDefault(user, new ArrayList<>());
    }

    public Set<String> getAllUsers() {
        return graph.keySet();
    }

    public int getDegree(String user) {
        return getFriends(user).size();
    }

    // --------------------------------------------------------
    // EXERCISE 3 : TRAVERSING ALGORITHMS (BFS)
    // --------------------------------------------------------

    // PART A: Basic BFS
    public List<String> bfs(String startUser) {
        if (!graph.containsKey(startUser)) return new ArrayList<>();

        List<String> order = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(startUser);
        visited.add(startUser);

        while (!queue.isEmpty()) {
            String currentUser = queue.poll();
            order.add(currentUser);

            for (String friend : getFriends(currentUser)) {
                if (!visited.contains(friend)) {
                    visited.add(friend);
                    queue.add(friend);
                }
            }
        }
        return order;
    }

    // PART B: BFS with Distances
    public Map<String, Integer> bfsWithDistances(String startUser) {
        if (!graph.containsKey(startUser)) return new HashMap<>();

        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        distances.put(startUser, 0);
        queue.add(startUser);

        while (!queue.isEmpty()) {
            String currentUser = queue.poll();

            for (String friend : getFriends(currentUser)) {
                if (!distances.containsKey(friend)) {
                    distances.put(friend, distances.get(currentUser) + 1);
                    queue.add(friend);
                }
            }
        }
        return distances;
    }

    // PART C: Shortest Path
    public List<String> shortestPath(String startUser, String targetUser) {
        if (!graph.containsKey(startUser) || !graph.containsKey(targetUser)) return new ArrayList<>();
        if (startUser.equals(targetUser)) return Arrays.asList(startUser);

        Map<String, String> parents = new HashMap<>();
        parents.put(startUser, null);
        Queue<String> queue = new LinkedList<>();
        queue.add(startUser);

        boolean found = false;

        while (!queue.isEmpty()) {
            String currentUser = queue.poll();

            if (currentUser.equals(targetUser)) {
                found = true;
                break;
            }

            for (String friend : getFriends(currentUser)) {
                if (!parents.containsKey(friend)) {
                    parents.put(friend, currentUser);
                    queue.add(friend);
                }
            }
        }

        if (!found) return new ArrayList<>(); // No existing path

        List<String> path = new ArrayList<>();
        String step = targetUser;
        while (step != null) {
            path.add(step);
            step = parents.get(step);
        }
        Collections.reverse(path);
        return path;
    }

    // PART D: Degrees of Separation
    public int degreesOfSeparation(String startUser, String targetUser) {
        if (!graph.containsKey(startUser) || !graph.containsKey(targetUser)) return -1;
        if (startUser.equals(targetUser)) return 0;

        Map<String, Integer> distances = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        distances.put(startUser, 0);
        queue.add(startUser);

        while (!queue.isEmpty()) {
            String currentUser = queue.poll();

            if (currentUser.equals(targetUser)) {
                return distances.get(currentUser);
            }

            for (String friend : getFriends(currentUser)) {
                if (!distances.containsKey(friend)) {
                    distances.put(friend, distances.get(currentUser) + 1);
                    queue.add(friend);
                }
            }
        }
        return -1; // Target not found
    }

    // PART E: Friends within K hops
    private static class NodePair {
        String user;
        int distance;
        NodePair(String u, int d) { user = u; distance = d; }
    }

    public Set<String> friendsWithinKHops(String startUser, int k) {
        if (!graph.containsKey(startUser)) return new HashSet<>();

        Set<String> kClosest = new HashSet<>();
        Queue<NodePair> queue = new LinkedList<>();

        kClosest.add(startUser);
        queue.add(new NodePair(startUser, 0));

        while (!queue.isEmpty()) {
            NodePair current = queue.poll();

            if (current.distance == k) break; // k hops limit reached, we don't look at further nodes

            for (String friend : getFriends(current.user)) {
                if (!kClosest.contains(friend)) {
                    kClosest.add(friend);
                    queue.add(new NodePair(friend, current.distance + 1));
                }
            }
        }
        kClosest.remove(startUser); // Removing the start user from the results
        return kClosest;
    }

    // ANALYTICS 1: Average Degrees of Separation
    public double computeAverageDegreesOfSeparation() {
        double totalDistance = 0;
        int totalPairs = 0;

        for (String user : getAllUsers()) {
            if (getDegree(user) == 0) continue; // Ignore isolated users

            Map<String, Integer> distances = bfsWithDistances(user);

            for (Map.Entry<String, Integer> entry : distances.entrySet()) {
                String target = entry.getKey();
                int distance = entry.getValue();

                // We use compareTo to make sure we count each unique pair only once
                // and not twice (forward, backward) needlessly.
                if (target.compareTo(user) > 0) {
                    totalDistance += distance;
                    totalPairs++;
                }
            }
        }

        if (totalPairs == 0) return 0.0;
        return totalDistance / totalPairs;
    }

    // ANALYTICS 2: Distance Distribution
    public Map<Integer, Integer> getDistanceDistribution(String startUser) {
        Map<String, Integer> distances = bfsWithDistances(startUser);
        Map<Integer, Integer> distribution = new HashMap<>();

        for (int distance : distances.values()) {
            if (distance == 0) continue; // Excluding the start user themselves
            distribution.put(distance, distribution.getOrDefault(distance, 0) + 1);
        }
        return distribution;
    }

    // ANALYTICS 3: Recommend Friends
    public List<String> recommendFriends(String startUser, int maxRecommendations) {
        if (!graph.containsKey(startUser)) return new ArrayList<>();

        List<String> directFriends = getFriends(startUser);
        Map<String, Integer> scores = new HashMap<>();

        for (String friend : directFriends) {
            for (String fof : getFriends(friend)) {
                if (!fof.equals(startUser) && !directFriends.contains(fof)) {
                    scores.put(fof, scores.getOrDefault(fof, 0) + 1);
                }
            }
        }

        // Sorting the dictionary in descending score order using Java streams
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(maxRecommendations)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    // --------------------------------------------------------
    // TESTS AND EDGE CASES
    // --------------------------------------------------------
    public static void main(String[] args) {
        exercise3_bfs sg = new exercise3_bfs();

        // Creating the social network
        sg.addFriendship("Alice", "Bob");
        sg.addFriendship("Bob", "Charlie");
        sg.addFriendship("Charlie", "Dave");

        // Shortcut : Alice has a 2 hops path to Dave via Eve
        sg.addFriendship("Alice", "Eve");
        sg.addFriendship("Eve", "Dave");

        // Isolated User
        sg.addUser("Frank");

        System.out.println("--- TESTS BFS ET EDGE CASES ---");

        // 1. Basic BFS Edge Case : Unexisting user
        System.out.println("1. BFS (Ghost): " + sg.bfs("Ghost")); // Output must be empty

        // 2. Shortest Path : Alice -> Dave (Make sur it takes the path via Eve and not Charlie)
        System.out.println("2. Shortest Path (Alice -> Dave): " + sg.shortestPath("Alice", "Dave")); // [Alice, Eve, Dave]

        // 3. Shortest Path Edge Cases : To herself and to an inexisting path
        System.out.println("3. Shortest Path (Alice -> Alice): " + sg.shortestPath("Alice", "Alice")); // [Alice]
        System.out.println("   Shortest Path (Alice -> Frank): " + sg.shortestPath("Alice", "Frank")); // []

        // 4. Degrees of Separation : Inexisting path
        System.out.println("4. Degrees (Alice -> Frank): " + sg.degreesOfSeparation("Alice", "Frank")); // -1

        // 5. Friends within k hops : Exploration limit
        System.out.println("5. Friends 1 hop (Alice): " + sg.friendsWithinKHops("Alice", 1)); // [Bob, Eve]
        System.out.println("   Friends 2 hops (Alice): " + sg.friendsWithinKHops("Alice", 2)); // [Bob, Eve, Charlie, Dave]

        // 6. Average Degrees of Separation (Complete Graph)
        System.out.println("6. Average Degrees: " + String.format("%.2f", sg.computeAverageDegreesOfSeparation()));

        // 7. Distribution of distances
        System.out.println("7. Distance Distribution (Alice): " + sg.getDistanceDistribution("Alice")); // 1 hop: 2, 2 hops: 2

        // 8. Friend recommendation with scores :
        // Alice knows Bob and Eve.
        // Bob's friends : Charlie. -> Charlie has 1 common friend with Alice
        // Eve's friends : Dave. -> Dave has 1 common friend with  Alice
        // Add another common friend to force the sorting :
        sg.addFriendship("Bob", "Dave"); // Now Dave is friends with Eve AND Bob.
        System.out.println("8. Recommendations (Alice): " + sg.recommendFriends("Alice", 5)); // [Dave, Charlie] because Dave has 2 common friends, Charlie has 1.
    }
}