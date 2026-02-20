def merge_intervals(intervals):
    if intervals == []:
        return []
    intervals.sort()
    result = []
    current_start = intervals[0][0]
    current_end = intervals[0][1]
    for i in range(1, len(intervals)):
        start = intervals[i][0]
        end = intervals[i][1]
        if start <= current_end:
            if end > current_end:
                current_end = end
        else:
            result.append([current_start, current_end])
            current_start = start
            current_end = end

    result.append([current_start, current_end])

    return result


print(merge_intervals([]))
print(merge_intervals([[1, 2]]))
print(merge_intervals([[1, 2], [3, 4], [5, 6]]))
print(merge_intervals([[1, 3], [2, 6]]))
print(merge_intervals([[1, 4], [4, 5]]))
print(merge_intervals([[8, 10], [1, 3], [2, 6], [15, 18]]))
print(merge_intervals([[1, 4], [1, 4], [1, 4]]))
print(merge_intervals([[1, 10], [2, 3], [4, 8]]))
print(merge_intervals([[1, 2], [2, 3], [3, 4]]))
print(merge_intervals([[-10, -5], [-6, 0], [1, 2]]))
print(merge_intervals([[0, 0], [0, 1]]))

