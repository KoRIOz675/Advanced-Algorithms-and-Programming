
import java.util.ArrayList;

public class exercise4_mutual_followers_matrix {

    int matrixSize;
    boolean[][] followerMatrix;

    public static void main(String[] args) {
        exercise4_mutual_followers_matrix socialNetwork = new exercise4_mutual_followers_matrix(5);

        socialNetwork.follow(0, 1);
        socialNetwork.follow(1, 0);
        socialNetwork.follow(0, 2);
        socialNetwork.follow(2, 0);
        socialNetwork.follow(1, 2);
        socialNetwork.follow(2, 1);
        socialNetwork.follow(3, 4);

        System.err.println("Is user 0 following user 1? " + socialNetwork.is_following(0, 1)); // true
        System.err.println("Is user 1 following user 0? " + socialNetwork.is_following(1, 0)); // true
        System.err.println("Is user 3 following user 4? " + socialNetwork.is_following(3, 4)); // true
        System.err.println("Is user 4 following user 3? " + socialNetwork.is_following(4, 3)); // false

        System.err.println("Followers of user 0: " + socialNetwork.get_followers(0)); // [1, 2]
        System.err.println("Following of user 0: " + socialNetwork.get_following(0)); // [1, 2]

        System.err.println("Mutual followers: ");
        for (int[] pair : socialNetwork.mutual_follow()) {
            System.err.println("User " + pair[0] + " and User " + pair[1]);
        }
        // Output:
        // User 0 and User 1
        // User 0 and User 2
        // User 1 and User 2

        System.err.println("Influence score of user 0: " + socialNetwork.influence_score(0)); // (2 followers + 2 following) / 5 = 0.8
    }

    public exercise4_mutual_followers_matrix(int matrixSize) {
        this.matrixSize = matrixSize;
        this.followerMatrix = new boolean[matrixSize][matrixSize];
    }

    // set follow to TRUE from follower -> followee
    public void follow(int follower, int followee) {
        if (follower < 0 || follower >= matrixSize || followee < 0 || followee >= matrixSize) {
            return;
        }

        if (follower == followee) {
            // A user cannot follow itself
            followerMatrix[follower][followee] = false;
        } else {
            followerMatrix[follower][followee] = true;
        }
    }

    // set follow to FALSE from follower -> followee
    public void unfollow(int follower, int followee) {
        if (follower < 0 || follower >= matrixSize || followee < 0 || followee >= matrixSize) {
            return;
        }

        followerMatrix[follower][followee] = false;
    }

    // check if follower follows followee
    public boolean is_following(int follower, int followee) {
        if (follower < 0 || follower >= matrixSize || followee < 0 || followee >= matrixSize) {
            return false;
        }

        if (follower == followee) {
            return false; // A user cannot follow itself
        } else {
            return followerMatrix[follower][followee];
        }
    }

    // list of users following this user
    public ArrayList<Integer> get_followers(int user) {
        ArrayList<Integer> followers = new ArrayList<>();

        if (user < 0 || user >= matrixSize) {
            return followers;
        }

        for (int item = 0; item < matrixSize; item++) {
            if (followerMatrix[item][user]) {
                followers.add(item);
            }
        }
        return followers;
    }

    // list of users this user follows
    public ArrayList<Integer> get_following(int user) {
        ArrayList<Integer> following = new ArrayList<>();

        if (user < 0 || user >= matrixSize) {
            return following;
        }

        for (int item = 0; item < matrixSize; item++) {
            if (followerMatrix[user][item]) {
                following.add(item);
            }
        }
        return following;
    }

    // list of mutual pairs [userA, userB] where both follow each other
    public ArrayList<int[]> mutual_follow() {
        ArrayList<int[]> mutuals = new ArrayList<>();

        for (int item1 = 0; item1 < matrixSize; item1++) {
            for (int item2 = item1 + 1; item2 < matrixSize; item2++) {
                if (followerMatrix[item1][item2] && followerMatrix[item2][item1]) {
                    mutuals.add(new int[]{item1, item2});
                }
            }
        }

        return mutuals;
    }

    // influence score of a user
    public double influence_score(int user) {
        int follower_count = get_followers(user).size();
        int following_count = get_following(user).size();
        return (double) (follower_count + following_count) / matrixSize;
    }
    
}
