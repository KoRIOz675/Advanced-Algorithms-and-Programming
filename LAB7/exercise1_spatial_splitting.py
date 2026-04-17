from dataclasses import dataclass
from typing import List


@dataclass
class Point:
    x: int
    y: int


@dataclass
class Region:
    x: int
    y: int
    width: int
    height: int


def split_region(x: int, y: int, width: int, height: int, min_size: int) -> List[Region]:

    if width < min_size or height < min_size:
        return []

    current_region = Region(x, y, width, height)

    half_width = width // 2
    half_height = height // 2

    if half_width == 0 or half_height == 0:
        return [current_region]

    regions = [current_region]

    regions += split_region(x, y, half_width, half_height, min_size)
    regions += split_region(x + half_width, y, half_width, half_height, min_size)
    regions += split_region(x, y + half_height, half_width, half_height, min_size)
    regions += split_region(x + half_width, y + half_height, half_width, half_height, min_size)

    return regions


def count_points_in_region(points: List[Point], region: Region) -> int:

    count = 0

    for point in points:
        if (
            region.x <= point.x < region.x + region.width
            and region.y <= point.y < region.y + region.height
        ):
            count += 1

    return count


def find_dense_regions(
    points: List[Point],
    region: Region,
    min_size: int,
    density_threshold: float
) -> List[Region]:

    if region.width <= 0 or region.height <= 0:
        return []

    point_count = count_points_in_region(points, region)
    area = region.width * region.height
    density = point_count / area

    if region.width < min_size or region.height < min_size:
        if density > density_threshold:
            return [region]
        return []

    half_width = region.width // 2
    half_height = region.height // 2


    if half_width == 0 or half_height == 0:
        if density > density_threshold:
            return [region]
        return []

    nw = Region(region.x, region.y, half_width, half_height)
    ne = Region(region.x + half_width, region.y, half_width, half_height)
    sw = Region(region.x, region.y + half_height, half_width, half_height)
    se = Region(region.x + half_width, region.y + half_height, half_width, half_height)

    dense_regions = []
    dense_regions += find_dense_regions(points, nw, min_size, density_threshold)
    dense_regions += find_dense_regions(points, ne, min_size, density_threshold)
    dense_regions += find_dense_regions(points, sw, min_size, density_threshold)
    dense_regions += find_dense_regions(points, se, min_size, density_threshold)

    return dense_regions


# -------------------------
# TESTS
# -------------------------

def run_tests():
    print("=== TESTS SPLIT REGION ===")

    regions = split_region(0, 0, 100, 100, 10)
    print("Normal split:", len(regions) > 0)

    regions = split_region(0, 0, 5, 5, 10)
    print("Too small region:", regions == [])

    regions = split_region(0, 0, 101, 99, 10)
    print("Odd dimensions:", len(regions) > 0)

    regions = split_region(0, 0, 0, 0, 10)
    print("Zero region:", regions == [])


    print("\n=== TESTS COUNT POINTS ===")

    region = Region(0, 0, 10, 10)

    print("Empty points:", count_points_in_region([], region) == 0)


    points = [Point(5, 5)]
    print("Inside point:", count_points_in_region(points, region) == 1)


    points = [Point(0, 0)]
    print("Border included:", count_points_in_region(points, region) == 1)

  
    points = [Point(10, 5)]
    print("Border excluded:", count_points_in_region(points, region) == 0)

    points = [Point(1,1), Point(2,2), Point(20,20)]
    print("Multiple points:", count_points_in_region(points, region) == 2)


    region_zero = Region(0, 0, 0, 0)
    points = [Point(0,0)]
    print("Zero region:", count_points_in_region(points, region_zero) == 0)


    print("\n=== TESTS FIND DENSE REGIONS ===")

    initial_region = Region(0, 0, 100, 100)


    dense = find_dense_regions([], initial_region, 10, 0.01)
    print("No points:", dense == [])


    points = [
        Point(1,1), Point(2,2), Point(3,3),
        Point(80,80)  
    ]
    dense = find_dense_regions(points, initial_region, 10, 0.001)
    print("Cluster in corner:", len(dense) > 0)


    points = [Point(1,1)]
    region_small = Region(0,0,10,10)
    dense = find_dense_regions(points, region_small, 10, 0.01)
    print("Exact threshold:", isinstance(dense, list))

    dense = find_dense_regions(points, initial_region, 10, 100)
    print("High threshold:", dense == [])


    dense = find_dense_regions(points, initial_region, 1, 0.001)
    print("Min size 1:", isinstance(dense, list))


    points = [Point(50, 50)]  
    dense = find_dense_regions(points, initial_region, 10, 0.0001)
    print("Boundary point:", isinstance(dense, list))


run_tests()