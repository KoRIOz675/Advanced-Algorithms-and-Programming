import sys
from dataclasses import dataclass, field
from typing import List, Optional


@dataclass
class Comment:
    id: str
    text: str
    replies: List["Comment"] = field(default_factory=list)

    def __repr__(self):
        return f"Comment({self.id!r})"


def flatten_recursive(comment: Comment) -> List[Comment]:
    result: List[Comment] = []
    result.append(comment)
    for reply in comment.replies:
        result = result + flatten_recursive(reply)
    return result


STATE_START = "STATE_START"
STATE_REPLIES_DONE = "STATE_REPLIES_DONE"

def flatten_iterative(comment: Comment) -> List[Comment]:
    stack: List[tuple] = []
    result: List[Comment] = []
    stack.append((comment, STATE_START))
    while stack:
        current_node, state = stack.pop()
        if state == STATE_START:
            result.append(current_node)
            stack.append((current_node, STATE_REPLIES_DONE))
            for i in range(len(current_node.replies) - 1, -1, -1):
                stack.append((current_node.replies[i], STATE_START))
        elif state == STATE_REPLIES_DONE:
            continue
    return result


def count_comments_tail(
    comment: Comment,
    accumulator: int = 0,
    _pending: Optional[List[Comment]] = None,
) -> int:
    accumulator += 1
    if _pending is None:
        _pending = []
    if not comment.replies:
        if not _pending:
            return accumulator
        else:
            next_node = _pending.pop()
            return count_comments_tail(next_node, accumulator, _pending)
    else:
        remaining_nodes = list(comment.replies) + _pending
        next_node = remaining_nodes.pop()
        return count_comments_tail(next_node, accumulator, remaining_nodes)


def count_comments_loop(comment: Comment) -> int:
    accumulator = 0
    stack: List[Comment] = [comment]
    while stack:
        current_comment = stack.pop()
        accumulator += 1
        for reply in current_comment.replies:
            stack.append(reply)
    return accumulator


def build_thread() -> Comment:
    r1a1 = Comment("r1a1", "deep reply")
    r1a  = Comment("r1a",  "reply 1a", replies=[r1a1])
    r1b  = Comment("r1b",  "reply 1b")
    r1   = Comment("r1",   "reply 1",  replies=[r1a, r1b])
    r2a  = Comment("r2a",  "reply 2a")
    r2   = Comment("r2",   "reply 2",  replies=[r2a])
    root = Comment("root", "root comment", replies=[r1, r2])
    return root


if __name__ == "__main__":
    root         = build_thread()
    leaf         = Comment("lone", "no replies")
    single_reply = Comment("p", "parent", replies=[Comment("c", "child")])

    expected_order = ["root", "r1", "r1a", "r1a1", "r1b", "r2", "r2a"]
    expected_count = 7

    print("=== flatten_recursive ===", file=sys.stderr)
    result_rec = flatten_recursive(root)
    ids_rec    = [c.id for c in result_rec]
    print(f"Full thread DFS order : {ids_rec}", file=sys.stderr)                                          # ['root', 'r1', 'r1a', 'r1a1', 'r1b', 'r2', 'r2a']
    assert ids_rec == expected_order
    print(f"Single node           : {[c.id for c in flatten_recursive(leaf)]}", file=sys.stderr)          # ['lone']
    assert [c.id for c in flatten_recursive(leaf)] == ["lone"]
    print(f"One-level deep        : {[c.id for c in flatten_recursive(single_reply)]}", file=sys.stderr)  # ['p', 'c']
    assert [c.id for c in flatten_recursive(single_reply)] == ["p", "c"]
    print("flatten_recursive: all assertions passed ✓\n", file=sys.stderr)

    print("=== flatten_iterative ===", file=sys.stderr)
    result_iter = flatten_iterative(root)
    ids_iter    = [c.id for c in result_iter]
    print(f"Full thread DFS order : {ids_iter}", file=sys.stderr)                                          # ['root', 'r1', 'r1a', 'r1a1', 'r1b', 'r2', 'r2a']
    assert ids_iter == expected_order
    print(f"Single node           : {[c.id for c in flatten_iterative(leaf)]}", file=sys.stderr)           # ['lone']
    assert [c.id for c in flatten_iterative(leaf)] == ["lone"]
    print(f"One-level deep        : {[c.id for c in flatten_iterative(single_reply)]}", file=sys.stderr)   # ['p', 'c']
    assert [c.id for c in flatten_iterative(single_reply)] == ["p", "c"]
    assert ids_rec == ids_iter
    print("flatten_iterative: all assertions passed ✓\n", file=sys.stderr)

    print("=== count_comments_tail ===", file=sys.stderr)
    print(f"Full thread count     : {count_comments_tail(root)}", file=sys.stderr)          # 7
    assert count_comments_tail(root) == expected_count
    print(f"Single node count     : {count_comments_tail(leaf)}", file=sys.stderr)          # 1
    assert count_comments_tail(leaf) == 1
    print(f"One-level deep count  : {count_comments_tail(single_reply)}", file=sys.stderr)  # 2
    assert count_comments_tail(single_reply) == 2
    print("count_comments_tail: all assertions passed ✓\n", file=sys.stderr)

    print("=== count_comments_loop ===", file=sys.stderr)
    print(f"Full thread count     : {count_comments_loop(root)}", file=sys.stderr)          # 7
    assert count_comments_loop(root) == expected_count
    print(f"Single node count     : {count_comments_loop(leaf)}", file=sys.stderr)          # 1
    assert count_comments_loop(leaf) == 1
    print(f"One-level deep count  : {count_comments_loop(single_reply)}", file=sys.stderr)  # 2
    assert count_comments_loop(single_reply) == 2
    assert count_comments_tail(root) == count_comments_loop(root)
    print("count_comments_loop: all assertions passed ✓\n", file=sys.stderr)

    print("=== Cross-validation ===", file=sys.stderr)
    assert len(flatten_recursive(root)) == count_comments_loop(root)
    print(f"len(flatten_recursive) == count_comments_loop == {expected_count}  ✓", file=sys.stderr)
    print("\nAll tests passed ✓", file=sys.stderr)