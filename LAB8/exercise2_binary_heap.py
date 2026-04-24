from dataclasses import dataclass
import math
import random
import time
import unittest


@dataclass
class Post:
    likes: int
    post_id: int
    timestamp: int


class TrendingHeap:
    def __init__(self):
        self.heap = []
        self.count = 0

    def parent(self, i):
        return (i - 1) // 2

    def left(self, i):
        return 2 * i + 1

    def right(self, i):
        return 2 * i + 2

    def swap(self, i, j):
        self.heap[i], self.heap[j] = self.heap[j], self.heap[i]

    def heapify_up(self, i):
        while i > 0 and self.heap[self.parent(i)].likes < self.heap[i].likes:
            p = self.parent(i)
            self.swap(i, p)
            i = p

    def max_heapify(self, i):
        largest = i
        left = self.left(i)
        right = self.right(i)

        if left < self.count and self.heap[left].likes > self.heap[largest].likes:
            largest = left

        if right < self.count and self.heap[right].likes > self.heap[largest].likes:
            largest = right

        if largest != i:
            self.swap(i, largest)
            self.max_heapify(largest)

    def push(self, post_id, likes, timestamp):
        new_post = Post(likes, post_id, timestamp)
        self.heap.append(new_post)
        self.count += 1
        self.heapify_up(self.count - 1)

    def peek_max(self):
        if self.count == 0:
            return None
        return self.heap[0]

    def pop_max(self):
        if self.count == 0:
            return None

        max_post = self.heap[0]

        if self.count == 1:
            self.heap.pop()
            self.count = 0
            return max_post

        self.heap[0] = self.heap[self.count - 1]
        self.heap.pop()
        self.count -= 1
        self.max_heapify(0)

        return max_post

    def size(self):
        return self.count

    def find_post_index(self, post_id):
        for i in range(self.count):
            if self.heap[i].post_id == post_id:
                return i
        return -1

    def update_likes(self, post_id, new_likes, timestamp):
        index = self.find_post_index(post_id)

        if index == -1:
            return

        old_likes = self.heap[index].likes
        self.heap[index].likes = new_likes
        self.heap[index].timestamp = timestamp

        if new_likes > old_likes:
            self.heapify_up(index)
        elif new_likes < old_likes:
            self.max_heapify(index)

    def get_top_k(self, k):
        if k <= 0:
            return []

        temp_heap = TrendingHeap()

        for post in self.heap:
            temp_heap.push(post.post_id, post.likes, post.timestamp)

        result = []
        limit = min(k, temp_heap.count)

        for _ in range(limit):
            result.append(temp_heap.pop_max())

        return result

    def is_valid_heap(self):
        for i in range(self.count // 2):
            left = self.left(i)
            right = self.right(i)

            if left < self.count and self.heap[i].likes < self.heap[left].likes:
                return False

            if right < self.count and self.heap[i].likes < self.heap[right].likes:
                return False

        return True

    def get_height(self):
        if self.count == 0:
            return 0
        return math.floor(math.log2(self.count)) + 1

    def get_level_order(self):
        return self.heap

    def remove_old_posts(self, current_time):
        i = 0

        while i < self.count:
            if current_time - self.heap[i].timestamp > 24:
                self.heap[i] = self.heap[self.count - 1]
                self.heap.pop()
                self.count -= 1

                if i < self.count:
                    self.max_heapify(i)
                    self.heapify_up(i)
            else:
                i += 1


def simulate_trending_feed():
    H = TrendingHeap()

    for i in range(1, 101):
        likes = random.randint(0, 1000)
        timestamp = int(time.time())
        H.push(i, likes, timestamp)

    for step in range(1, 10001):
        random_post_id = random.randint(1, 100)
        new_likes = random.randint(0, 1000)
        timestamp = int(time.time())

        H.update_likes(random_post_id, new_likes, timestamp)

        if step % 1000 == 0:
            top5 = H.get_top_k(5)
            print(f"\nAfter {step} updates:")
            for post in top5:
                print(post)


class TestTrendingHeapEdgeCases(unittest.TestCase):

    def test_empty_heap(self):
        h = TrendingHeap()

        self.assertEqual(h.size(), 0)
        self.assertIsNone(h.peek_max())
        self.assertIsNone(h.pop_max())
        self.assertEqual(h.get_top_k(5), [])
        self.assertTrue(h.is_valid_heap())
        self.assertEqual(h.get_height(), 0)

    def test_one_post(self):
        h = TrendingHeap()
        h.push(1, 100, 10)

        self.assertEqual(h.size(), 1)
        self.assertEqual(h.peek_max().post_id, 1)
        self.assertEqual(h.pop_max().post_id, 1)
        self.assertEqual(h.size(), 0)

    def test_pop_max_order(self):
        h = TrendingHeap()
        h.push(1, 10, 10)
        h.push(2, 50, 10)
        h.push(3, 30, 10)

        self.assertEqual(h.pop_max().post_id, 2)
        self.assertEqual(h.pop_max().post_id, 3)
        self.assertEqual(h.pop_max().post_id, 1)
        self.assertIsNone(h.pop_max())

    def test_peek_does_not_remove(self):
        h = TrendingHeap()
        h.push(1, 10, 10)
        h.push(2, 50, 10)

        self.assertEqual(h.peek_max().post_id, 2)
        self.assertEqual(h.size(), 2)

    def test_get_top_k_does_not_destroy_heap(self):
        h = TrendingHeap()
        h.push(1, 10, 10)
        h.push(2, 50, 10)
        h.push(3, 30, 10)

        top2 = h.get_top_k(2)

        self.assertEqual([p.post_id for p in top2], [2, 3])
        self.assertEqual(h.size(), 3)
        self.assertEqual(h.peek_max().post_id, 2)

    def test_get_top_k_larger_than_heap(self):
        h = TrendingHeap()
        h.push(1, 10, 10)
        h.push(2, 20, 10)

        top10 = h.get_top_k(10)

        self.assertEqual(len(top10), 2)
        self.assertEqual([p.post_id for p in top10], [2, 1])

    def test_get_top_k_zero_or_negative(self):
        h = TrendingHeap()
        h.push(1, 10, 10)

        self.assertEqual(h.get_top_k(0), [])
        self.assertEqual(h.get_top_k(-3), [])

    def test_update_likes_increase(self):
        h = TrendingHeap()
        h.push(1, 10, 10)
        h.push(2, 20, 10)
        h.push(3, 30, 10)

        h.update_likes(1, 100, 20)

        self.assertEqual(h.peek_max().post_id, 1)
        self.assertTrue(h.is_valid_heap())

    def test_update_likes_decrease(self):
        h = TrendingHeap()
        h.push(1, 100, 10)
        h.push(2, 50, 10)
        h.push(3, 30, 10)

        h.update_likes(1, 5, 20)

        self.assertNotEqual(h.peek_max().post_id, 1)
        self.assertTrue(h.is_valid_heap())

    def test_update_non_existing_post(self):
        h = TrendingHeap()
        h.push(1, 100, 10)

        h.update_likes(999, 500, 20)

        self.assertEqual(h.size(), 1)
        self.assertEqual(h.peek_max().post_id, 1)
        self.assertTrue(h.is_valid_heap())

    def test_duplicate_likes(self):
        h = TrendingHeap()
        h.push(1, 50, 10)
        h.push(2, 50, 10)
        h.push(3, 50, 10)

        self.assertTrue(h.is_valid_heap())
        self.assertEqual(h.size(), 3)

    def test_negative_likes(self):
        h = TrendingHeap()
        h.push(1, -10, 10)
        h.push(2, -5, 10)
        h.push(3, -30, 10)

        self.assertEqual(h.peek_max().post_id, 2)
        self.assertTrue(h.is_valid_heap())

    def test_remove_old_posts(self):
        h = TrendingHeap()
        current_time = 100

        h.push(1, 100, 90)
        h.push(2, 200, 70)
        h.push(3, 300, 95)
        h.push(4, 400, 60)

        h.remove_old_posts(current_time)

        remaining_ids = [post.post_id for post in h.heap]

        self.assertNotIn(2, remaining_ids)
        self.assertNotIn(4, remaining_ids)
        self.assertIn(1, remaining_ids)
        self.assertIn(3, remaining_ids)
        self.assertTrue(h.is_valid_heap())

    def test_manual_build_heap(self):
        h = TrendingHeap()
        h.heap = [
            Post(10, 1, 10),
            Post(80, 2, 10),
            Post(30, 3, 10),
            Post(100, 4, 10),
        ]
        h.count = len(h.heap)

        for i in range(h.count // 2 - 1, -1, -1):
            h.max_heapify(i)

        self.assertEqual(h.peek_max().post_id, 4)
        self.assertTrue(h.is_valid_heap())


if __name__ == "__main__":
    unittest.main()