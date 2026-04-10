class SocialGraph:
    def __init__(self):
        self.adj_list = {}

    def add_edge(self, u, v):
        if u not in self.adj_list:
            self.adj_list[u] = []
        if v not in self.adj_list:
            self.adj_list[v] = []
        self.adj_list[u].append(v)
        self.adj_list[v].append(u)


def print_graph_nice(G):
    print("\n=== GRAPH STRUCTURE ===")
    if not G.adj_list:
        print("Empty graph")
        return

    for u in sorted(G.adj_list):
        neighbors = sorted(G.adj_list[u])
        if neighbors:
            print(f"User {u} -> {neighbors}")
        else:
            print(f"User {u} -> [No friends]")


def dfs_recursive(G, start_user):
    result = []
    visited = {}

    def dfs(u):
        visited[u] = True
        result.append(u)

        if u in G.adj_list:
            for v in G.adj_list[u]:
                if v not in visited:
                    dfs(v)

    if start_user in G.adj_list:
        dfs(start_user)

    return result


def dfs_iterative(G, start_user):
    result = []
    visited = {}
    stack = [start_user]

    while stack:
        u = stack.pop()

        if u not in visited:
            visited[u] = True
            result.append(u)

            if u in G.adj_list:
                for v in G.adj_list[u]:
                    if v not in visited:
                        stack.append(v)

    return result


def find_connected_components(G):
    components = []
    visited = {}

    def dfs(u, current):
        visited[u] = True
        current.append(u)

        if u in G.adj_list:
            for v in G.adj_list[u]:
                if v not in visited:
                    dfs(v, current)

    for u in G.adj_list:
        if u not in visited:
            current = []
            dfs(u, current)
            components.append(current)

    return components


def is_connected(G):
    components = find_connected_components(G)
    return len(components) == 1


def has_path(G, start_user, target_user):
    visited = {}
    stack = [start_user]

    while stack:
        u = stack.pop()

        if u == target_user:
            return True

        if u not in visited:
            visited[u] = True

            if u in G.adj_list:
                for v in G.adj_list[u]:
                    if v not in visited:
                        stack.append(v)

    return False


def find_path(G, start_user, target_user):
    visited = {}
    parent = {}
    stack = [start_user]

    parent[start_user] = None

    while stack:
        u = stack.pop()

        if u not in visited:
            visited[u] = True

            if u == target_user:
                break

            if u in G.adj_list:
                for v in G.adj_list[u]:
                    if v not in parent:
                        parent[v] = u
                        stack.append(v)

    if target_user not in parent:
        return []

    path = []
    current = target_user

    while current is not None:
        path.append(current)
        current = parent[current]

    path.reverse()
    return path


def get_connected_components_sizes(G):
    components = find_connected_components(G)
    return [len(c) for c in components]


def find_largest_component(G):
    components = find_connected_components(G)
    largest = []
    for c in components:
        if len(c) > len(largest):
            largest = c
    return largest


def find_isolated_users(G):
    result = []
    for u in G.adj_list:
        if len(G.adj_list[u]) == 0:
            result.append(u)
    return result


def test():
    G = SocialGraph()

    # Graph normal
    G.add_edge(1, 2)
    G.add_edge(2, 3)
    G.add_edge(4, 5)

    # Edge case: isolated node
    G.adj_list[6] = []

    print_graph_nice(G)

    print("\nDFS Recursive:", dfs_recursive(G, 1))
    print("DFS Iterative:", dfs_iterative(G, 1))

    print("\nConnected Components:", find_connected_components(G))
    print("Is Connected:", is_connected(G))

    print("\nHas Path (1->3):", has_path(G, 1, 3))
    print("Has Path (1->5):", has_path(G, 1, 5))

    print("\nFind Path (1->3):", find_path(G, 1, 3))
    print("Find Path (1->5):", find_path(G, 1, 5))

    print("\nComponent Sizes:", get_connected_components_sizes(G))
    print("Largest Component:", find_largest_component(G))
    print("Isolated Users:", find_isolated_users(G))

    # Edge case: empty graph
    empty = SocialGraph()
    print("\n--- EMPTY GRAPH TEST ---")
    print_graph_nice(empty)
    print("Empty Graph Components:", find_connected_components(empty))

    # Edge case: single node
    single = SocialGraph()
    single.adj_list[1] = []
    print("\n--- SINGLE NODE TEST ---")
    print_graph_nice(single)
    print("Single Node DFS:", dfs_recursive(single, 1))


test()