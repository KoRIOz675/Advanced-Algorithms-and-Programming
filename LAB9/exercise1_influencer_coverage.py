from itertools import combinations


def is_valid_coverage(selected_users, graph):
    covered = set()

    for user in selected_users:
        covered.add(user)

        for neighbor in graph[user]:
            covered.add(neighbor)

    return len(covered) == len(graph)


def find_minimum_coverage(graph):
    users = list(graph.keys())
    n = len(users)

    for size in range(1, n + 1):
        for subset in combinations(users, size):
            if is_valid_coverage(subset, graph):
                return size, list(subset)

    return n, users


def find_fast_coverage(graph):
    selected = []
    uncovered = set(graph.keys())

    while len(uncovered) > 0:
        best_user = None
        best_cover = set()

        for user in graph:
            current_cover = set()

            if user in uncovered:
                current_cover.add(user)

            for neighbor in graph[user]:
                if neighbor in uncovered:
                    current_cover.add(neighbor)

            if len(current_cover) > len(best_cover):
                best_user = user
                best_cover = current_cover

        selected.append(best_user)
        uncovered = uncovered - best_cover

    return len(selected), selected


# =========================================================
# TESTS
# =========================================================

# ---------------------------------------------------------
# Test 1 : Simple line graph
# A - B - C - D
# ---------------------------------------------------------
graph1 = {
    "A": ["B"],
    "B": ["A", "C"],
    "C": ["B", "D"],
    "D": ["C"]
}

print("TEST 1 - Line graph")
print(is_valid_coverage(["B", "C"], graph1))
print(find_minimum_coverage(graph1))
print(find_fast_coverage(graph1))
print()


# ---------------------------------------------------------
# Test 2 : Fully connected graph
# Every node connected to every other node
# ---------------------------------------------------------
graph2 = {
    "A": ["B", "C", "D"],
    "B": ["A", "C", "D"],
    "C": ["A", "B", "D"],
    "D": ["A", "B", "C"]
}

print("TEST 2 - Fully connected graph")
print(is_valid_coverage(["A"], graph2))
print(find_minimum_coverage(graph2))
print(find_fast_coverage(graph2))
print()


# ---------------------------------------------------------
# Test 3 : Empty graph
# No edges
# ---------------------------------------------------------
graph3 = {
    "A": [],
    "B": [],
    "C": []
}

print("TEST 3 - Empty graph")
print(is_valid_coverage(["A"], graph3))
print(find_minimum_coverage(graph3))
print(find_fast_coverage(graph3))
print()


# ---------------------------------------------------------
# Test 4 : Single node graph
# ---------------------------------------------------------
graph4 = {
    "A": []
}

print("TEST 4 - Single node")
print(is_valid_coverage(["A"], graph4))
print(find_minimum_coverage(graph4))
print(find_fast_coverage(graph4))
print()


# ---------------------------------------------------------
# Test 5 : Star graph
#        A
#      / | \
#     B  C  D
# ---------------------------------------------------------
graph5 = {
    "A": ["B", "C", "D"],
    "B": ["A"],
    "C": ["A"],
    "D": ["A"]
}

print("TEST 5 - Star graph")
print(is_valid_coverage(["A"], graph5))
print(find_minimum_coverage(graph5))
print(find_fast_coverage(graph5))
print()


# ---------------------------------------------------------
# Test 6 : Disconnected graph
# A - B     C - D
# ---------------------------------------------------------
graph6 = {
    "A": ["B"],
    "B": ["A"],
    "C": ["D"],
    "D": ["C"]
}

print("TEST 6 - Disconnected graph")
print(is_valid_coverage(["A", "C"], graph6))
print(find_minimum_coverage(graph6))
print(find_fast_coverage(graph6))
print()


# ---------------------------------------------------------
# Test 7 : Invalid coverage
# ---------------------------------------------------------
print("TEST 7 - Invalid coverage")
print(is_valid_coverage(["A"], graph1))
print()