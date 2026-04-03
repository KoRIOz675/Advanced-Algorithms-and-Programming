from __future__ import annotations
from dataclasses import dataclass, field
from typing import Optional


# ─────────────────────────────────────────────
#  DATA STRUCTURE  (mirrors pseudo-code)
# ─────────────────────────────────────────────

@dataclass
class CategoryNode:
    category_id: str
    name: str
    post_count: int
    left: Optional["CategoryNode"] = None
    right: Optional["CategoryNode"] = None
    parent: Optional["CategoryNode"] = None

    def __repr__(self):
        return f"CategoryNode({self.name}, {self.post_count})"


# ─────────────────────────────────────────────
#  PART A — IN-ORDER TRAVERSAL
# ─────────────────────────────────────────────

def in_order_collect(node: Optional[CategoryNode]) -> list[str]:
    if node is None:
        return []
    result = []
    left_list = in_order_collect(node.left)
    for element in left_list:
        result.append(element)
    result.append(node.name)
    right_list = in_order_collect(node.right)
    for element in right_list:
        result.append(element)
    return result


def in_order_accumulate_posts(node: Optional[CategoryNode]) -> int:
    if node is None:
        return 0
    total = 0
    total += in_order_accumulate_posts(node.left)
    total += node.post_count
    total += in_order_accumulate_posts(node.right)
    return total


def in_order_find_kth(node: Optional[CategoryNode], k: int) -> Optional[str]:
    names = in_order_collect(node)
    if k <= 0 or k > len(names):
        return None
    return names[k - 1]


# ─────────────────────────────────────────────
#  PART B — PRE-ORDER TRAVERSAL
# ─────────────────────────────────────────────

def pre_order_export(node: Optional[CategoryNode]) -> str:
    if node is None:
        return ""
    text = node.name + "(" + str(node.post_count) + ")" + "\n"
    text += pre_order_export(node.left)
    text += pre_order_export(node.right)
    return text


def pre_order_copy(node: Optional[CategoryNode]) -> Optional[CategoryNode]:
    if node is None:
        return None
    new_node = CategoryNode(
        category_id=node.category_id,
        name=node.name,
        post_count=node.post_count,
        parent=None
    )
    new_node.left = pre_order_copy(node.left)
    if new_node.left is not None:
        new_node.left.parent = new_node
    new_node.right = pre_order_copy(node.right)
    if new_node.right is not None:
        new_node.right.parent = new_node
    return new_node


def pre_order_serialize(node: Optional[CategoryNode]) -> str:
    """Converts tree to 'Name(count)|...' string in pre-order."""
    if node is None:
        return ""
    text = node.name + "(" + str(node.post_count) + ")"
    left_text = pre_order_serialize(node.left)
    right_text = pre_order_serialize(node.right)
    if left_text != "":
        text = text + "|" + left_text
    if right_text != "":
        text = text + "|" + right_text
    return text


# ─────────────────────────────────────────────
#  PART C — POST-ORDER TRAVERSAL
# ─────────────────────────────────────────────

def post_order_total_posts(node: Optional[CategoryNode]) -> int:
    if node is None:
        return 0
    total = 0
    total += post_order_total_posts(node.left)
    total += post_order_total_posts(node.right)
    total += node.post_count
    return total


def post_order_average_depth(node: Optional[CategoryNode], depth: int = 0) -> tuple[int, int]:
    if node is None:
        return (0, 0)
    if node.left is None and node.right is None:
        return (depth, 1)
    left_result = post_order_average_depth(node.left, depth + 1)
    right_result = post_order_average_depth(node.right, depth + 1)
    total_depth = left_result[0] + right_result[0]
    leaf_count = left_result[1] + right_result[1]
    return (total_depth, leaf_count)


def post_order_collect_leaves(node: Optional[CategoryNode]) -> list[str]:
    if node is None:
        return []
    result = []
    left_list = post_order_collect_leaves(node.left)
    for element in left_list:
        result.append(element)
    right_list = post_order_collect_leaves(node.right)
    for element in right_list:
        result.append(element)
    if node.left is None and node.right is None:
        result.append(node.name)
    return result


# ─────────────────────────────────────────────
#  ANALYTICS
# ─────────────────────────────────────────────

def find_most_popular_category(node: Optional[CategoryNode]) -> Optional[CategoryNode]:
    if node is None:
        return None
    best = node
    left_best = find_most_popular_category(node.left)
    right_best = find_most_popular_category(node.right)
    if left_best is not None and left_best.post_count > best.post_count:
        best = left_best
    if right_best is not None and right_best.post_count > best.post_count:
        best = right_best
    return best


def category_with_most_subcategories(node: Optional[CategoryNode]) -> tuple[Optional[CategoryNode], int]:
    if node is None:
        return (None, 0)
    count = 0
    if node.left is not None:
        count += 1
    if node.right is not None:
        count += 1
    left = category_with_most_subcategories(node.left)
    right = category_with_most_subcategories(node.right)
    best = node
    max_count = count
    if left[1] > max_count:
        best = left[0]
        max_count = left[1]
    if right[1] > max_count:
        best = right[0]
        max_count = right[1]
    return (best, max_count)


# ─────────────────────────────────────────────
#  TEST HELPERS
# ─────────────────────────────────────────────

def print_header(title: str):
    print("\n" + "=" * 60)
    print(f"  {title}")
    print("=" * 60)

def print_test(label: str, result, expected=None):
    status = ""
    if expected is not None:
        status = "  ✓" if result == expected else f"  ✗ (expected {expected})"
    print(f"  {label:<45} {result}{status}")


def build_lab_tree() -> CategoryNode:
    """
    Builds the reference tree from the LAB:
              Technology(150)
             /               \\
       Programming(85)      Design(65)
       /        \\           /       \\
    Python(42)  Java(30)  UI/UX(38)  Graphics(22)
    /      \\
  Django(18) Flask(12)
    """
    django   = CategoryNode("6", "Django",      18)
    flask    = CategoryNode("7", "Flask",        12)
    python   = CategoryNode("3", "Python",       42, left=django, right=flask)
    django.parent = python
    flask.parent  = python

    java     = CategoryNode("4", "Java",         30)
    prog     = CategoryNode("2", "Programming",  85, left=python, right=java)
    python.parent = prog
    java.parent   = prog

    uiux     = CategoryNode("5", "UI/UX",        38)
    graphics = CategoryNode("8", "Graphics",     22)
    design   = CategoryNode("9", "Design",       65, left=uiux, right=graphics)
    uiux.parent     = design
    graphics.parent = design

    tech     = CategoryNode("1", "Technology",  150, left=prog, right=design)
    prog.parent   = tech
    design.parent = tech

    return tech


# ─────────────────────────────────────────────
#  TESTS
# ─────────────────────────────────────────────

def run_tests():
    root = build_lab_tree()
    single = CategoryNode("A", "Solo", 99)
    empty = None

    # ── Part A ────────────────────────────────
    print_header("PART A — In-order Traversal")

    print_test(
        "in_order_collect (lab tree)",
        in_order_collect(root),
        ["Django","Python","Flask","Programming","Java","Technology","UI/UX","Design","Graphics"]
    )
    print_test("in_order_collect (single node)",  in_order_collect(single), ["Solo"])
    print_test("in_order_collect (empty tree)",   in_order_collect(empty),  [])

    print_test("in_order_accumulate_posts (lab tree)",   in_order_accumulate_posts(root),   462)
    print_test("in_order_accumulate_posts (single)",     in_order_accumulate_posts(single), 99)
    print_test("in_order_accumulate_posts (empty)",      in_order_accumulate_posts(empty),  0)

    print_test("in_order_find_kth k=1 (Django)",         in_order_find_kth(root, 1), "Django")
    print_test("in_order_find_kth k=6 (Technology)",     in_order_find_kth(root, 6), "Technology")
    print_test("in_order_find_kth k=9 (Graphics)",       in_order_find_kth(root, 9), "Graphics")
    print_test("in_order_find_kth k=0  → NULL",          in_order_find_kth(root, 0), None)
    print_test("in_order_find_kth k=10 → NULL (out)",    in_order_find_kth(root, 10), None)
    print_test("in_order_find_kth empty tree",           in_order_find_kth(empty, 1), None)

    # ── Part B ────────────────────────────────
    print_header("PART B — Pre-order Traversal")

    export_out = pre_order_export(root)
    first_line = export_out.split("\n")[0]
    print_test("pre_order_export first line",  first_line, "Technology(150)")
    print_test("pre_order_export empty tree",  pre_order_export(empty), "")
    print_test("pre_order_export single node", pre_order_export(single).strip(), "Solo(99)")

    copy_root = pre_order_copy(root)
    print_test("pre_order_copy not same object",      copy_root is not root, True)
    print_test("pre_order_copy root name preserved",  copy_root.name, "Technology")
    print_test("pre_order_copy left child name",      copy_root.left.name, "Programming")
    print_test("pre_order_copy parent link set",      copy_root.left.parent is copy_root, True)
    print_test("pre_order_copy empty tree",           pre_order_copy(empty), None)

    print_test(
        "pre_order_serialize (lab tree)",
        pre_order_serialize(root),
        "Technology(150)|Programming(85)|Python(42)|Django(18)|Flask(12)|Java(30)|Design(65)|UI/UX(38)|Graphics(22)"
    )
    print_test("pre_order_serialize empty",  pre_order_serialize(empty), "")
    print_test("pre_order_serialize single", pre_order_serialize(single), "Solo(99)")

    # ── Part C ────────────────────────────────
    print_header("PART C — Post-order Traversal")

    print_test("post_order_total_posts (lab tree)",  post_order_total_posts(root),   462)
    print_test("post_order_total_posts (single)",    post_order_total_posts(single), 99)
    print_test("post_order_total_posts (empty)",     post_order_total_posts(empty),  0)

    td, lc = post_order_average_depth(root)
    avg = td / lc if lc else 0
    print_test("post_order_average_depth leaf_count",  lc,  5)
    print_test("post_order_average_depth total_depth", td, 12)   # 3+3+2+2+2=12 (depth from root=0)
    print_test("post_order_average_depth average",     round(avg, 2), 2.4)
    td2, lc2 = post_order_average_depth(single)
    print_test("post_order_average_depth single",      (td2, lc2), (0, 1))
    print_test("post_order_average_depth empty",       post_order_average_depth(empty), (0, 0))

    print_test(
        "post_order_collect_leaves (lab tree)",
        post_order_collect_leaves(root),
        ["Django","Flask","Java","UI/UX","Graphics"]
    )
    print_test("post_order_collect_leaves (single)", post_order_collect_leaves(single), ["Solo"])
    print_test("post_order_collect_leaves (empty)",  post_order_collect_leaves(empty),  [])

    # ── Analytics ─────────────────────────────
    print_header("ANALYTICS")

    most_pop = find_most_popular_category(root)
    print_test("find_most_popular_category name",  most_pop.name,       "Technology")
    print_test("find_most_popular_category count", most_pop.post_count, 150)
    print_test("find_most_popular_category single", find_most_popular_category(single).name, "Solo")
    print_test("find_most_popular_category empty",  find_most_popular_category(empty), None)

    best_node, best_cnt = category_with_most_subcategories(root)
    print_test("category_with_most_subcategories name",  best_node.name, "Technology")
    print_test("category_with_most_subcategories count", best_cnt,       2)
    n, c = category_with_most_subcategories(single)
    print_test("category_with_most_subcategories single count", c, 0)
    n2, c2 = category_with_most_subcategories(empty)
    print_test("category_with_most_subcategories empty", (n2, c2), (None, 0))

    print("\n" + "=" * 60)
    print("  All tests done.")
    print("=" * 60 + "\n")


if __name__ == "__main__":
    run_tests()