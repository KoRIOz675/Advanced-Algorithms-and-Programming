from __future__ import annotations
from dataclasses import dataclass
from typing import Optional, List, Dict
import unittest
import sys


# =========================
# Part A - Activity Stack
# =========================

@dataclass
class ActivityStackPointer:
    activity: str
    nextElem: Optional["ActivityStackPointer"] = None


class ActivityStack:
    def __init__(self) -> None:
        self.top: Optional[ActivityStackPointer] = None
        self._size: int = 0

    # O(1)
    def push(self, activity: str) -> "ActivityStack":
        node = ActivityStackPointer(activity=activity, nextElem=self.top)
        self.top = node
        self._size += 1
        return self

    # O(1)
    def pop(self) -> Optional[str]:
        if self.top is None:
            return None
        node = self.top
        self.top = node.nextElem
        self._size -= 1
        return node.activity

    # O(1)
    def peek(self) -> Optional[str]:
        if self.top is None:
            return None
        return self.top.activity

    # O(1)
    def is_empty(self) -> bool:
        return self.top is None

    # O(1)
    def size(self) -> int:
        return self._size

    # O(n) where n is requested count (or until stack ends)
    def display_recent(self, n: int) -> List[str]:
        if self.top is None or n <= 0:
            return []
        items: List[str] = []
        current = self.top
        shown = 0
        while current is not None and shown < n:
            items.append(current.activity)
            current = current.nextElem
            shown += 1
        return items


# =========================
# Part B - Notification Queue (doubly linked list)
# =========================

@dataclass
class NotificationQueuePointer:
    notification: str
    prevElem: Optional["NotificationQueuePointer"] = None
    nextElem: Optional["NotificationQueuePointer"] = None


class NotificationQueue:
    def __init__(self) -> None:
        self.head: Optional[NotificationQueuePointer] = None
        self.tail: Optional[NotificationQueuePointer] = None
        self._size: int = 0

    # O(1)
    def enqueue(self, notification: str) -> "NotificationQueue":
        node = NotificationQueuePointer(notification=notification)
        if self.tail is None:
            self.head = node
            self.tail = node
        else:
            node.prevElem = self.tail
            self.tail.nextElem = node
            self.tail = node
        self._size += 1
        return self

    # O(1) - returns oldest notification (useful for processing)
    def dequeue(self) -> Optional[str]:
        if self.head is None:
            return None
        node = self.head
        self.head = node.nextElem
        if self.head is not None:
            self.head.prevElem = None
        else:
            self.tail = None
        self._size -= 1
        return node.notification

    # O(1)
    def front(self) -> Optional[str]:
        if self.head is None:
            return None
        return self.head.notification

    # O(1)
    def is_empty(self) -> bool:
        return self.head is None

    # O(1)
    def size(self) -> int:
        return self._size

    # O(n)
    def display_pending(self) -> List[str]:
        if self.head is None:
            return []
        items: List[str] = []
        current = self.head
        while current is not None:
            items.append(current.notification)
            current = current.nextElem
        return items

    # O(1) - insert at front (priority boost)
    def priority_enqueue(self, notification: str) -> "NotificationQueue":
        node = NotificationQueuePointer(notification=notification)
        if self.head is None:
            self.head = node
            self.tail = node
        else:
            node.nextElem = self.head
            self.head.prevElem = node
            self.head = node
        self._size += 1
        return self


# =========================
# Part C - Feed Processor
# =========================

class FeedProcessor:
    def __init__(self) -> None:
        self.recent_activities = ActivityStack()
        self.notification_queue = NotificationQueue()
        self.processed_log = NotificationQueue()

    # O(1)
    def process_incoming(self) -> Optional[str]:
        notification = self.notification_queue.dequeue()
        if notification is None:
            print("No notification to process")
            return None
        self.recent_activities.push(notification)
        return notification

    # O(k)
    def batch_process(self, k: int) -> int:
        processed = 0
        for i in range(1, k + 1):
            if self.notification_queue.is_empty():
                print(f"Queue empty after {i - 1} items")
                return processed
            self.process_incoming()
            processed += 1
        return processed

    # O(n), n = size of recent stack
    def clear_history(self) -> int:
        moved = 0
        while not self.recent_activities.is_empty():
            activity = self.recent_activities.pop()
            if activity is not None:
                self.processed_log.enqueue(activity)
                moved += 1
        return moved

    # O(1)
    def get_stats(self) -> Dict[str, int]:
        return {
            "recent": self.recent_activities.size(),
            "pending": self.notification_queue.size(),
            "processed": self.processed_log.size(),
        }


# =========================
# Tests
# =========================

def run_demo() -> None:
    print("=== Activity Stack ===", file=sys.stderr)
    stack = ActivityStack()
    print(f"Initial empty? {stack.is_empty()} | size={stack.size()}", file=sys.stderr)

    stack.push("like").push("comment").push("share")
    print(f"Top activity: {stack.peek()}", file=sys.stderr)  # share
    print(f"2 most recent: {stack.display_recent(2)}", file=sys.stderr)  # ['share', 'comment']
    print(f"Pop: {stack.pop()}", file=sys.stderr)  # share
    print(f"After pop -> top: {stack.peek()} | size={stack.size()}", file=sys.stderr)

    print("\n=== Notification Queue ===", file=sys.stderr)
    queue = NotificationQueue()
    queue.enqueue("notif-1").enqueue("notif-2").enqueue("notif-3")
    print(f"Pending: {queue.display_pending()}", file=sys.stderr)  # ['notif-1', 'notif-2', 'notif-3']
    print(f"Front: {queue.front()}", file=sys.stderr)  # notif-1
    print(f"Dequeue: {queue.dequeue()}", file=sys.stderr)  # notif-1
    print(f"After dequeue: {queue.display_pending()}", file=sys.stderr)  # ['notif-2', 'notif-3']

    queue.priority_enqueue("URGENT")
    print(f"After priority enqueue: {queue.display_pending()}", file=sys.stderr)  # ['URGENT', 'notif-2', 'notif-3']

    print("\n=== Feed Processor ===", file=sys.stderr)
    fp = FeedProcessor()
    fp.notification_queue.enqueue("A").enqueue("B").enqueue("C")
    print(f"Initial stats: {fp.get_stats()}", file=sys.stderr)  # recent=0, pending=3, processed=0

    fp.process_incoming()  # moves A to recent stack top
    print(f"After process_incoming stats: {fp.get_stats()}", file=sys.stderr)

    processed = fp.batch_process(5)  # only B, C left
    print(f"Batch processed count: {processed}", file=sys.stderr)  # 2
    print(f"Stats before clear_history: {fp.get_stats()}", file=sys.stderr)  # recent=3, pending=0, processed=0

    moved = fp.clear_history()
    print(f"Moved to processed_log: {moved}", file=sys.stderr)  # 3
    print(f"Final stats: {fp.get_stats()}", file=sys.stderr)  # recent=0, pending=0, processed=3

    print("\n=== Edge Cases ===", file=sys.stderr)
    empty_stack = ActivityStack()
    empty_queue = NotificationQueue()
    empty_fp = FeedProcessor()

    print(f"Empty stack pop: {empty_stack.pop()}", file=sys.stderr)  # None
    print(f"Empty stack peek: {empty_stack.peek()}", file=sys.stderr)  # None
    print(f"Empty stack recent(3): {empty_stack.display_recent(3)}", file=sys.stderr)  # []

    print(f"Empty queue front: {empty_queue.front()}", file=sys.stderr)  # None
    print(f"Empty queue dequeue: {empty_queue.dequeue()}", file=sys.stderr)  # None
    print(f"Empty queue pending: {empty_queue.display_pending()}", file=sys.stderr)  # []

    print(f"Empty feed process_incoming: {empty_fp.process_incoming()}", file=sys.stderr)  # None + message
    print(f"Empty feed batch_process(2): {empty_fp.batch_process(2)}", file=sys.stderr)  # 0 + message
    print(f"Empty feed clear_history: {empty_fp.clear_history()}", file=sys.stderr)  # 0
    print(f"Empty feed stats: {empty_fp.get_stats()}", file=sys.stderr)  # all 0


if __name__ == "__main__":
    run_demo()