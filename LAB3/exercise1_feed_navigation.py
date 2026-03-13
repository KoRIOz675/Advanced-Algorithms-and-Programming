class StoryNode:
    def __init__(self, story_id, user_id, content_preview, timestamp, views=0):
        self.story_id = story_id
        self.user_id = user_id
        self.content_preview = content_preview
        self.timestamp = timestamp
        self.views = views
        self.next = None
        self.prev = None


class DoublyLinkedList:
    def __init__(self):
        self.head = None
        self.tail = None
        self.current = None
        self.size = 0

    # O(1)
    def add_story(self, node):
        node.next = None
        node.prev = self.tail

        if self.tail is not None:
            self.tail.next = node
        else:
            self.head = node

        self.tail = node
        self.size += 1

    # O(n)
    def remove_story(self, story_id):
        p = self.head
        for i in range(self.size):
            if p is None:
                return
            if p.story_id == story_id:
                if p.prev is not None:
                    p.prev.next = p.next
                else:
                    self.head = p.next

                if p.next is not None:
                    p.next.prev = p.prev
                else:
                    self.tail = p.prev

                if self.current == p:
                    if p.next is not None:
                        self.current = p.next
                    else:
                        self.current = p.prev

                self.size -= 1
                return

            p = p.next

    # O(1)
    def move_forward(self):
        if self.current is not None and self.current.next is not None:
            self.current = self.current.next
        return self.current

    # O(1)
    def move_backward(self):
        if self.current is not None and self.current.prev is not None:
            self.current = self.current.prev
        return self.current
    # O(n)
    def jump_to(self, story_id):
        p = self.head
        while p is not None:
            if p.story_id == story_id:
                self.current = p
                return
            p = p.next

    # O(n)
    def insert_after(self, current_id, new_story):
        p = self.head
        while p is not None and p.story_id != current_id:
            p = p.next
        if p is None:
            return

        new_story.prev = p
        new_story.next = p.next
        new_story.views = 0

        if p.next is not None:
            p.next.prev = new_story
        else:
            self.tail = new_story

        p.next = new_story
        self.size += 1

    # O(k)
    def display_around_current(self, k):
        left = self.current
        count = 0

        while left is not None and left.prev is not None and count < k:
            left = left.prev
            count += 1

        p = left
        count = 0

        while p is not None and count < 2 * k + 1:
            print(p.story_id, p.content_preview, p.views)
            p = p.next
            count += 1

    # O(1)
    def track_view(self):
        if self.current is not None:
            self.current.views += 1

    # O(n)
    def most_viewed(self):
        if self.head is None:
            return None

        most_viewed = self.head
        p = self.head.next

        while p is not None:
            if p.views > most_viewed.views:
                most_viewed = p
            p = p.next

        return most_viewed

    # O(n**2)
    def reorder_by_views(self):
        changed = True

        while changed is True:
            changed = False
            p = self.head

            while p is not None and p.next is not None:
                if p.views < p.next.views:
                    temp_story_id = p.story_id
                    temp_user_id = p.user_id
                    temp_content_preview = p.content_preview
                    temp_timestamp = p.timestamp
                    temp_views = p.views

                    p.story_id = p.next.story_id
                    p.user_id = p.next.user_id
                    p.content_preview = p.next.content_preview
                    p.timestamp = p.next.timestamp
                    p.views = p.next.views

                    p.next.story_id = temp_story_id
                    p.next.user_id = temp_user_id
                    p.next.content_preview = temp_content_preview
                    p.next.timestamp = temp_timestamp
                    p.next.views = temp_views

                    changed = True

                p = p.next



# ---------------------------
# Testing the feed
# ---------------------------

def run_demo():
    feed = DoublyLinkedList()

    s1 = StoryNode(1, 101, "Morning coffee", "08:00")
    s2 = StoryNode(2, 102, "Workout complete", "09:00")
    s3 = StoryNode(3, 103, "Sunset photo", "18:00")

    feed.add_story(s1)
    feed.add_story(s2)
    feed.add_story(s3)

    print("Initial feed:")
    p = feed.head
    while p is not None:
        print(f"[Story{p.story_id}: {p.content_preview}] -> ", end="")
        p = p.next
    print("None")

    feed.jump_to(2)
    feed.track_view()
    feed.track_view()

    feed.jump_to(3)
    feed.track_view()

    print("\nViews after interactions:")
    p = feed.head
    while p is not None:
        print(f"Story{p.story_id} ({p.views})")
        p = p.next

    feed.reorder_by_views()

    print("\nFeed after reorder_by_views():")
    p = feed.head
    while p is not None:
        print(f"[Story{p.story_id}: {p.content_preview}] -> ", end="")
        p = p.next
    print("None")


if __name__ == "__main__":
    run_demo()