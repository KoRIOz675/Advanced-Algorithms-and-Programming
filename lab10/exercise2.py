def is_within_budget(selection, costs, budget):
    total_cost = 0

    for user in selection:
        total_cost += costs[user]

    return total_cost <= budget


def maximize_reach_exact(budget, costs, reaches):
    n = len(costs)

    if budget <= 0 or n == 0:
        return 0, []

    dp = [[0 for _ in range(budget + 1)] for _ in range(n + 1)]

    for i in range(1, n + 1):
        for b in range(budget + 1):
            if costs[i - 1] > b:
                dp[i][b] = dp[i - 1][b]
            else:
                dp[i][b] = max(
                    dp[i - 1][b],
                    dp[i - 1][b - costs[i - 1]] + reaches[i - 1]
                )

    selected_users = []
    b = budget

    for i in range(n, 0, -1):
        if dp[i][b] != dp[i - 1][b]:
            selected_users.append(i - 1)
            b -= costs[i - 1]

    selected_users.reverse()
    return dp[n][budget], selected_users


def maximize_reach_greedy(budget, costs, reaches):
    n = len(costs)

    if budget <= 0 or n == 0:
        return 0, []

    users = []

    for i in range(n):
        if costs[i] > 0:
            ratio = reaches[i] / costs[i]
        else:
            ratio = float("inf")

        users.append((i, ratio))

    users.sort(key=lambda x: x[1], reverse=True)

    selected_users = []
    total_reach = 0
    remaining_budget = budget

    for user, ratio in users:
        if costs[user] <= remaining_budget:
            selected_users.append(user)
            total_reach += reaches[user]
            remaining_budget -= costs[user]

    return total_reach, selected_users


# =========================
# PYTEST TESTS
# =========================

def test_is_within_budget_true():
    costs = [10, 20, 30]
    assert is_within_budget([0, 1], costs, 30) is True


def test_is_within_budget_false():
    costs = [10, 20, 30]
    assert is_within_budget([1, 2], costs, 40) is False


def test_exact_normal_case():
    costs = [10, 20, 30]
    reaches = [60, 100, 120]
    budget = 50

    max_reach, selected = maximize_reach_exact(budget, costs, reaches)

    assert max_reach == 220
    assert set(selected) == {1, 2}


def test_greedy_counterexample():
    costs = [10, 20, 30]
    reaches = [60, 100, 120]
    budget = 50

    greedy_reach, greedy_selected = maximize_reach_greedy(budget, costs, reaches)
    exact_reach, exact_selected = maximize_reach_exact(budget, costs, reaches)

    assert greedy_reach == 160
    assert exact_reach == 220
    assert greedy_reach < exact_reach


def test_budget_zero():
    costs = [10, 20, 30]
    reaches = [60, 100, 120]

    assert maximize_reach_exact(0, costs, reaches) == (0, [])
    assert maximize_reach_greedy(0, costs, reaches) == (0, [])


def test_empty_lists():
    costs = []
    reaches = []
    budget = 100

    assert maximize_reach_exact(budget, costs, reaches) == (0, [])
    assert maximize_reach_greedy(budget, costs, reaches) == (0, [])


def test_no_item_fits():
    costs = [100, 200, 300]
    reaches = [10, 20, 30]
    budget = 50

    assert maximize_reach_exact(budget, costs, reaches) == (0, [])
    assert maximize_reach_greedy(budget, costs, reaches) == (0, [])


def test_single_item_fits():
    costs = [50]
    reaches = [100]
    budget = 50

    assert maximize_reach_exact(budget, costs, reaches) == (100, [0])
    assert maximize_reach_greedy(budget, costs, reaches) == (100, [0])


def test_single_item_does_not_fit():
    costs = [60]
    reaches = [100]
    budget = 50

    assert maximize_reach_exact(budget, costs, reaches) == (0, [])
    assert maximize_reach_greedy(budget, costs, reaches) == (0, [])


def test_all_items_fit():
    costs = [10, 20, 30]
    reaches = [5, 10, 15]
    budget = 60

    assert maximize_reach_exact(budget, costs, reaches) == (30, [0, 1, 2])
    assert maximize_reach_greedy(budget, costs, reaches) == (30, [0, 1, 2])