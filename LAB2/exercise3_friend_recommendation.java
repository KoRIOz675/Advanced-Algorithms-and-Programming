package LAB2;

import java.util.*;

public class exercise3_friend_recommendation {

	public static void main(String[] args) {
		// --- 1. SETUP EXAMPLE DATA ---
		double[] user0 = {10, 0, 8, 2, 5, 7};
		double[] user1 = {9, 1, 7, 3, 6, 8};
		double[] user2 = {2, 9, 1, 8, 3, 0};
		List<double[]> allUsers = Arrays.asList(user0, user1, user2);

		// --- 2. TEST REQUIREMENT 2: SIMILARITY ---
		System.out.println("--- Cosine Similarity Tests ---");
		System.out.printf("Similarity (User0, User1): %.2f (Expected ~0.94)%n", similarity(user0, user1));
		System.out.printf("Similarity (User0, User2): %.2f (Expected ~0.18)%n", similarity(user0, user2));

		// --- 3. TEST REQUIREMENT 3: K-SIMILAR USERS  ---
		System.out.println("\n--- Top K-Similar Users (k=1) ---");
		Set<double[]> existingFriends = new HashSet<>(); // Assume no friends yet
		List<double[]> topK = kSimilar(user0, 1, allUsers, existingFriends);
		System.out.println("Top match for User0 is " + (Arrays.equals(topK.get(0), user1) ? "User1" : "User2"));

		// --- 4. TEST REQUIREMENT 4: RECOMMENDATIONS  ---
		System.out.println("\n--- Collaborative Filtering Recommendations ---");
		// User0 has a 0 at index 1 (Sports).
		Map<Integer, Double> recs = recommend(user0, topK);
		System.out.println("Recommended interest index 1 strength: " + recs.get(1));

		// --- 5. TEST BONUS: DIMENSIONALITY REDUCTION  ---
		System.out.println("\n--- Dimensionality Reduction Bonus ---");
		// Group indices 0 & 2 (Music & Tech), and 3 & 4 (Fashion & Travel)
		List<int[]> groups = Arrays.asList(new int[]{0, 2}, new int[]{3, 4});
		List<double[]> reduced = reduceDimensions(allUsers, groups);
		System.out.println("User0 reduced profile: " + Arrays.toString(reduced.get(0)));

		// --- 6. EDGE CASE: EMPTY USER ---
		double[] emptyUser = {0, 0, 0, 0, 0, 0};
		System.out.println("\n--- Edge Case: Empty User ---");
		System.out.println("Similarity with empty user: " + similarity(user0, emptyUser));
	}

	// --- YOUR EXISTING FUNCTIONS (MADE STATIC FOR MAIN) ---

	public static double similarity(double[] m1, double[] m2) {
		double dotProduct = 0, norm1 = 0, norm2 = 0;
		for (int i = 0; i < m1.length; i++) {
			dotProduct += m1[i] * m2[i];
			norm1 += Math.pow(m1[i], 2);
			norm2 += Math.pow(m2[i], 2);
		}
		if (norm1 == 0 || norm2 == 0) return 0;
		return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
	}

	public static List<double[]> kSimilar(double[] user, int k, List<double[]> allUsers, Set<double[]> existingFriends) {
		PriorityQueue<UserSimilarity> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.score));
		for (double[] otherUser : allUsers) {
			if (otherUser == user || existingFriends.contains(otherUser)) continue;
			double score = similarity(user, otherUser);
			pq.offer(new UserSimilarity(otherUser, score));
			if (pq.size() > k) pq.poll();
		}
		List<double[]> result = new ArrayList<>();
		while (!pq.isEmpty()) result.add(0, pq.poll().userProfile);
		return result;
	}

	public static Map<Integer, Double> recommend(double[] user, List<double[]> similarUsers) {
		Map<Integer, Double> potentials = new HashMap<>();
		if (similarUsers.isEmpty()) return potentials;
		for (int i = 0; i < user.length; i++) {
			if (user[i] == 0) {
				double totalStrength = 0;
				for (double[] similar : similarUsers) totalStrength += similar[i];
				potentials.put(i, totalStrength / similarUsers.size());
			}
		}
		return potentials;
	}

	public static List<double[]> reduceDimensions(List<double[]> fullMatrix, List<int[]> interestGroups) {
		List<double[]> reducedMatrix = new ArrayList<>();
		for (double[] userProfile : fullMatrix) {
			double[] reducedProfile = new double[interestGroups.size()];
			for (int g = 0; g < interestGroups.size(); g++) {
				double groupSum = 0;
				for (int interestIdx : interestGroups.get(g)) groupSum += userProfile[interestIdx];
				reducedProfile[g] = groupSum / interestGroups.get(g).length;
			}
			reducedMatrix.add(reducedProfile);
		}
		return reducedMatrix;
	}

	private static class UserSimilarity {
		double[] userProfile;
		double score;
		UserSimilarity(double[] u, double s) { this.userProfile = u; this.score = s; }
	}
}