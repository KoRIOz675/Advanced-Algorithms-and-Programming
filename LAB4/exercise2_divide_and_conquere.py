from dataclasses import dataclass
from typing import List


# =========================
# Structure
# =========================

@dataclass
class Post:
    post_id: int
    user_id: int
    content_preview: str
    timestamp: str
    likes: int
    comments: int
    shares: int
    engagement_score: int


# =========================
# Part A: Max Engagement
# =========================

def max_engagement(posts: List[Post], left: int, right: int) -> Post:
    if left == right:
        return posts[left]

    middle = (left + right) // 2
    left_post = max_engagement(posts, left, middle)
    right_post = max_engagement(posts, middle + 1, right)

    if left_post.engagement_score >= right_post.engagement_score:
        return left_post
    return right_post


# =========================
# Part B: Sum & Average
# =========================

def sum_engagement(posts: List[Post], left: int, right: int) -> int:
    if left == right:
        return posts[left].engagement_score

    middle = (left + right) // 2
    left_sum = sum_engagement(posts, left, middle)
    right_sum = sum_engagement(posts, middle + 1, right)

    return left_sum + right_sum


def average_engagement(posts: List[Post], left: int, right: int) -> float:
    total = sum_engagement(posts, left, right)
    count = right - left + 1
    return total / count


# =========================
# Part C: Count Threshold
# =========================

def count_above_threshold(posts: List[Post], left: int, right: int, threshold: int) -> int:
    if left == right:
        return 1 if posts[left].engagement_score > threshold else 0

    middle = (left + right) // 2
    left_count = count_above_threshold(posts, left, middle, threshold)
    right_count = count_above_threshold(posts, middle + 1, right, threshold)

    return left_count + right_count


# =========================
# Part D: Merge Sort
# =========================

def merge(posts: List[Post], left: int, middle: int, right: int) -> None:
    left_part = posts[left:middle + 1]
    right_part = posts[middle + 1:right + 1]

    i = 0
    j = 0
    k = left

    while i < len(left_part) and j < len(right_part):
        if left_part[i].engagement_score <= right_part[j].engagement_score:
            posts[k] = left_part[i]
            i += 1
        else:
            posts[k] = right_part[j]
            j += 1
        k += 1

    while i < len(left_part):
        posts[k] = left_part[i]
        i += 1
        k += 1

    while j < len(right_part):
        posts[k] = right_part[j]
        j += 1
        k += 1


def merge_sort_by_engagement(posts: List[Post], left: int, right: int) -> None:
    if left < right:
        middle = (left + right) // 2
        merge_sort_by_engagement(posts, left, middle)
        merge_sort_by_engagement(posts, middle + 1, right)
        merge(posts, left, middle, right)


# =========================
# Part E: Peak Hour
# =========================

def find_peak_hour(hourly_likes: List[int], left: int, right: int) -> int:
    if left == right:
        return left

    middle = (left + right) // 2

    if hourly_likes[middle] < hourly_likes[middle + 1]:
        return find_peak_hour(hourly_likes, middle + 1, right)
    return find_peak_hour(hourly_likes, left, middle)


# =========================
# Edge Cases / Tests
# =========================

def run_edge_cases() -> None:
    print("========== EDGE CASES / TESTS ==========\n")

    # -------------------------
    # max_engagement
    # -------------------------
    print("1) max_engagement")

    # Edge case: one single post
    posts_single = [
        Post(1, 1, "Only post", "2026-03-20 10:00", 0, 0, 0, 50)
    ]
    result = max_engagement(posts_single, 0, 0)
    print("Single post -> expected 50 | got:", result.engagement_score)

    # Edge case: two posts
    posts_two = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 50),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 80),
    ]
    result = max_engagement(posts_two, 0, 1)
    print("Two posts -> expected 80 | got:", result.engagement_score)

    # Edge case: same maximum score
    posts_same_max = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 100),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 100),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 70),
    ]
    result = max_engagement(posts_same_max, 0, 2)
    print("Same max score -> expected first max post_id = 1 | got:", result.post_id)

    # Edge case: all identical
    posts_identical = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 40),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 40),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 40),
    ]
    result = max_engagement(posts_identical, 0, 2)
    print("All identical -> expected first post_id = 1 | got:", result.post_id)

    # Edge case: negative values
    posts_negative = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, -10),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, -3),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, -20),
    ]
    result = max_engagement(posts_negative, 0, 2)
    print("Negative scores -> expected -3 | got:", result.engagement_score)
    print()

    # -------------------------
    # sum_engagement
    # -------------------------
    print("2) sum_engagement")

    # Edge case: one single post
    result = sum_engagement(posts_single, 0, 0)
    print("Single post -> expected 50 | got:", result)

    # Edge case: all zero
    posts_zero = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 0),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 0),
    ]
    result = sum_engagement(posts_zero, 0, 1)
    print("All zero -> expected 0 | got:", result)

    # Edge case: negative values
    posts_negative_sum = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, -5),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, -10),
    ]
    result = sum_engagement(posts_negative_sum, 0, 1)
    print("Negative values -> expected -15 | got:", result)

    # Edge case: mixed positive and negative
    posts_mixed = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 20),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, -5),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 10),
    ]
    result = sum_engagement(posts_mixed, 0, 2)
    print("Mixed values -> expected 25 | got:", result)
    print()

    # -------------------------
    # average_engagement
    # -------------------------
    print("3) average_engagement")

    # Edge case: one single post
    posts_avg_single = [
        Post(1, 1, "Only post", "2026-03-20 10:00", 0, 0, 0, 80)
    ]
    result = average_engagement(posts_avg_single, 0, 0)
    print("Single post -> expected 80.0 | got:", result)

    # Edge case: decimal result
    posts_avg_decimal = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 100),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 101),
    ]
    result = average_engagement(posts_avg_decimal, 0, 1)
    print("Decimal average -> expected 100.5 | got:", result)

    # Edge case: identical values
    posts_avg_identical = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 30),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 30),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 30),
    ]
    result = average_engagement(posts_avg_identical, 0, 2)
    print("Identical values -> expected 30.0 | got:", result)

    # Edge case: negative values
    posts_avg_negative = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, -10),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, -20),
    ]
    result = average_engagement(posts_avg_negative, 0, 1)
    print("Negative values -> expected -15.0 | got:", result)
    print()

    # -------------------------
    # count_above_threshold
    # -------------------------
    print("4) count_above_threshold")

    # Edge case: none above threshold
    posts_none_above = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 10),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 20),
    ]
    result = count_above_threshold(posts_none_above, 0, 1, 30)
    print("None above threshold -> expected 0 | got:", result)

    # Edge case: all above threshold
    result = count_above_threshold(posts_none_above, 0, 1, 5)
    print("All above threshold -> expected 2 | got:", result)

    # Edge case: equal to threshold
    posts_equal_threshold = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 100),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 101),
    ]
    result = count_above_threshold(posts_equal_threshold, 0, 1, 100)
    print("Equal to threshold -> expected 1 | got:", result)

    # Edge case: negative threshold
    posts_negative_threshold = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, -5),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 10),
    ]
    result = count_above_threshold(posts_negative_threshold, 0, 1, -1)
    print("Negative threshold -> expected 1 | got:", result)

    # Edge case: all equal to threshold
    posts_all_equal_threshold = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 50),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 50),
    ]
    result = count_above_threshold(posts_all_equal_threshold, 0, 1, 50)
    print("All equal to threshold -> expected 0 | got:", result)
    print()

    # -------------------------
    # merge_sort_by_engagement
    # -------------------------
    print("5) merge_sort_by_engagement")

    # Edge case: empty array
    posts_empty = []
    if len(posts_empty) > 0:
        merge_sort_by_engagement(posts_empty, 0, len(posts_empty) - 1)
    print("Empty array -> expected [] | got:", posts_empty)

    # Edge case: one single element
    posts_sort_single = [
        Post(1, 1, "Only post", "2026-03-20 10:00", 0, 0, 0, 50)
    ]
    merge_sort_by_engagement(posts_sort_single, 0, len(posts_sort_single) - 1)
    print("Single element -> expected [50] | got:", [p.engagement_score for p in posts_sort_single])

    # Edge case: already sorted
    posts_sorted = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 10),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 20),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 30),
    ]
    merge_sort_by_engagement(posts_sorted, 0, len(posts_sorted) - 1)
    print("Already sorted -> expected [10, 20, 30] | got:", [p.engagement_score for p in posts_sorted])

    # Edge case: reverse sorted
    posts_reverse = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 30),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 20),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 10),
    ]
    merge_sort_by_engagement(posts_reverse, 0, len(posts_reverse) - 1)
    print("Reverse sorted -> expected [10, 20, 30] | got:", [p.engagement_score for p in posts_reverse])

    # Edge case: duplicates
    posts_duplicates = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, 20),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 10),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, 20),
    ]
    merge_sort_by_engagement(posts_duplicates, 0, len(posts_duplicates) - 1)
    print("Duplicates -> expected [10, 20, 20] | got:", [p.engagement_score for p in posts_duplicates])

    # Edge case: negative values
    posts_sort_negative = [
        Post(1, 1, "Post A", "2026-03-20 10:00", 0, 0, 0, -5),
        Post(2, 2, "Post B", "2026-03-20 11:00", 0, 0, 0, 10),
        Post(3, 3, "Post C", "2026-03-20 12:00", 0, 0, 0, -2),
    ]
    merge_sort_by_engagement(posts_sort_negative, 0, len(posts_sort_negative) - 1)
    print("Negative values -> expected [-5, -2, 10] | got:", [p.engagement_score for p in posts_sort_negative])
    print()

    # -------------------------
    # find_peak_hour
    # -------------------------
    print("6) find_peak_hour")

    # Edge case: one single element
    likes_single = [42]
    result = find_peak_hour(likes_single, 0, 0)
    print("Single hour -> expected index 0 | got:", result)

    # Edge case: clear peak in the middle
    likes_peak_middle = [5, 10, 20, 30, 25, 15, 8]
    result = find_peak_hour(likes_peak_middle, 0, len(likes_peak_middle) - 1)
    print("Peak in middle -> expected index 3 | got:", result, "| value:", likes_peak_middle[result])

    # Edge case: strictly increasing
    likes_increasing = [1, 2, 3, 4, 5]
    result = find_peak_hour(likes_increasing, 0, len(likes_increasing) - 1)
    print("Strictly increasing -> expected index 4 | got:", result, "| value:", likes_increasing[result])

    # Edge case: strictly decreasing
    likes_decreasing = [9, 7, 5, 3, 1]
    result = find_peak_hour(likes_decreasing, 0, len(likes_decreasing) - 1)
    print("Strictly decreasing -> expected index 0 | got:", result, "| value:", likes_decreasing[result])

    # Edge case: plateau peak
    likes_plateau = [1, 3, 5, 5, 4, 2]
    result = find_peak_hour(likes_plateau, 0, len(likes_plateau) - 1)
    print("Plateau peak -> expected index 2 or 3 | got:", result, "| value:", likes_plateau[result])

    # Edge case: non-unimodal array (result not guaranteed to be the global maximum)
    likes_non_unimodal = [30, 12, 27, 2, 27, 10, 29]
    result = find_peak_hour(likes_non_unimodal, 0, len(likes_non_unimodal) - 1)
    print("Non-unimodal array -> local peak only, global max not guaranteed | got index:", result, "| value:", likes_non_unimodal[result])

    # Edge case: multiple peaks
    likes_multiple_peaks = [2, 8, 3, 9, 4, 7]
    result = find_peak_hour(likes_multiple_peaks, 0, len(likes_multiple_peaks) - 1)
    print("Multiple peaks -> result not guaranteed as global max | got index:", result, "| value:", likes_multiple_peaks[result])

    # Edge case: empty array
    likes_empty = []
    if len(likes_empty) == 0:
        print("Empty array -> invalid input (function should not be called)")
    print()

    print("========== END OF EDGE CASES / TESTS ==========")


if __name__ == "__main__":
    run_edge_cases()