import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TerrainGenerator
 * ----------------
 * Native Java implementation (no external libraries) of:
 *   1) Midpoint Displacement (1D profile generation)
 *   2) Terrain Generation (2D Diamond-Square style)
 *   3) Artifact Detection (abrupt height changes)
 *
 * Structure follows the provided pseudo-code as closely as possible.
 */
public class TerrainGenerator {

    // Fixed seed for reproducible runs. Remove the seed for random outputs.
    private static final Random RNG = new Random(42);

    // ==========================================================
    // Simple Point class (no external library)
    // ==========================================================
    static class Point {
        double x;
        double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", x, y);
        }
    }

    // ==========================================================
    // 1) MIDPOINT DISPLACEMENT FUNCTION
    // ==========================================================
    public static List<Point> midpointDisplacement(double x1, double y1,
                                                   double x2, double y2,
                                                   double roughness,
                                                   int depth) {
        List<Point> result = new ArrayList<>();

        // Step 1: base case - depth reached 0, return just the two endpoints
        if (depth == 0) {
            result.add(new Point(x1, y1));
            result.add(new Point(x2, y2));
            return result;
        }

        // Step 2: determine the midpoints
        double mx = (x1 + x2) / 2.0;
        double my = (y1 + y2) / 2.0;

        // Step 3: add a random offset to the midpoints
        mx = mx + roughness * randomMinusOneToOne();
        my = my + roughness * randomMinusOneToOne();

        // Step 4: recursive call on the left segment
        List<Point> leftPoints = midpointDisplacement(
                x1, y1, mx, my, roughness / 2.0, depth - 1);

        // Step 5: recursive call on the right segment
        List<Point> rightPoints = midpointDisplacement(
                mx, my, x2, y2, roughness / 2.0, depth - 1);

        // Step 6: merge, removing the duplicated midpoint
        result.addAll(leftPoints);
        for (int i = 1; i < rightPoints.size(); i++) {
            result.add(rightPoints.get(i));
        }

        return result;
    }

    // ==========================================================
    // 2) GENERATE TERRAIN FUNCTION
    // ==========================================================
    public static double[][] generateTerrain(int width, int height,
                                             double roughness, int depth) {
        // Step 1: initialize the grid, all values = 0 (corners stay at 0)
        double[][] grid = new double[height][width];
        grid[0][0] = 0;
        grid[0][width - 1] = 0;
        grid[height - 1][0] = 0;
        grid[height - 1][width - 1] = 0;

        int step = width - 1;

        // Step 2: base case
        if (depth == 0 || step <= 1) {
            return grid;
        }

        diamondSquare(grid, width, height, step, roughness, depth);
        return grid;
    }

    private static void diamondSquare(double[][] grid, int width, int height,
                                      int step, double roughness, int depth) {
        if (depth == 0 || step <= 1) {
            return;
        }

        int half = step / 2;

        // Step 3 (square step): center of each square
        for (int r = 0; r + step < height; r += step) {
            for (int c = 0; c + step < width; c += step) {
                double avg = (grid[r][c]
                        + grid[r][c + step]
                        + grid[r + step][c]
                        + grid[r + step][c + step]) / 4.0;
                grid[r + half][c + half] = avg + roughness * randomMinusOneToOne();
            }
        }

        // Step 4 (diamond step): edge midpoints
        for (int r = 0; r < height; r += half) {
            int startCol = ((r / half) % 2 == 0) ? half : 0;
            for (int c = startCol; c < width; c += step) {
                double sum = 0;
                int count = 0;

                if (r - half >= 0)     { sum += grid[r - half][c]; count++; }
                if (r + half < height) { sum += grid[r + half][c]; count++; }
                if (c - half >= 0)     { sum += grid[r][c - half]; count++; }
                if (c + half < width)  { sum += grid[r][c + half]; count++; }

                if (count > 0) {
                    double avg = sum / count;
                    grid[r][c] = avg + roughness * randomMinusOneToOne();
                }
            }
        }

        // Step 5: recursive call
        diamondSquare(grid, width, height, half, roughness / 2.0, depth - 1);
    }

    // ==========================================================
    // 3) DETECT ARTIFACTS FUNCTION
    // ==========================================================
    public static List<int[]> detectArtifacts(double[][] terrainGrid,
                                              double threshold) {
        List<int[]> result = new ArrayList<>();

        if (terrainGrid == null || terrainGrid.length == 0
                || terrainGrid[0].length == 0) {
            return result;
        }

        int H = terrainGrid.length;
        int W = terrainGrid[0].length;

        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {

                // Step 3: right neighbor
                if (col + 1 < W) {
                    if (Math.abs(terrainGrid[row][col]
                            - terrainGrid[row][col + 1]) > threshold) {
                        result.add(new int[]{row, col});
                        continue;
                    }
                }

                // Step 4: bottom neighbor
                if (row + 1 < H) {
                    if (Math.abs(terrainGrid[row][col]
                            - terrainGrid[row + 1][col]) > threshold) {
                        result.add(new int[]{row, col});
                    }
                }
            }
        }

        return result;
    }

    // ==========================================================
    // Helper utilities
    // ==========================================================
    private static double randomMinusOneToOne() {
        return RNG.nextDouble() * 2.0 - 1.0;
    }

    private static void printGrid(double[][] grid) {
        if (grid == null || grid.length == 0) {
            System.out.println("(empty grid)");
            return;
        }
        for (int r = 0; r < grid.length; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < grid[r].length; c++) {
                sb.append(String.format("%7.2f ", grid[r][c]));
            }
            System.out.println(sb.toString());
        }
    }

    private static void printPoints(List<Point> points) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(points.get(i));
        }
        System.out.println(sb.toString());
    }

    private static void printArtifacts(List<int[]> artifacts) {
        if (artifacts.isEmpty()) {
            System.out.println("  none");
            return;
        }
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < artifacts.size(); i++) {
            if (i > 0) sb.append(", ");
            int[] coord = artifacts.get(i);
            sb.append("(").append(coord[0]).append(",").append(coord[1]).append(")");
        }
        System.out.println(sb.toString());
    }

    // ==========================================================
    // MAIN - basic cases and edge cases
    // ==========================================================
    public static void main(String[] args) {

        // --- Midpoint Displacement: basic case ---
        System.out.println("[T1] Midpoint | basic | (0,0)->(8,0) r=2.0 d=3");
        List<Point> p1 = midpointDisplacement(0, 0, 8, 0, 2.0, 3);
        System.out.println("  points=" + p1.size() + " (expected 9)");
        printPoints(p1);

        // --- Midpoint Displacement: edge case depth=0 ---
        System.out.println("\n[T2] Midpoint | edge: depth=0 | (0,0)->(10,5) r=1.0");
        List<Point> p2 = midpointDisplacement(0, 0, 10, 5, 1.0, 0);
        System.out.println("  points=" + p2.size() + " (expected 2)");
        printPoints(p2);

        // --- Midpoint Displacement: edge case roughness=0 ---
        System.out.println("\n[T3] Midpoint | edge: roughness=0 | (0,0)->(8,0) d=3");
        List<Point> p3 = midpointDisplacement(0, 0, 8, 0, 0.0, 3);
        printPoints(p3);

        // --- Midpoint Displacement: edge case roughness=2 ---
        System.out.println("\n[T4] Midpoint | edge: roughness=2 | (0,0)->(8,0) d=4");
        List<Point> p4 = midpointDisplacement(0, 0, 8, 0, 2.0, 4);
        System.out.println("  points=" + p4.size() + " (expected 17)");
        printPoints(p4);

        // --- Generate Terrain: basic 5x5 ---
        System.out.println("\n[T5] Terrain | basic | 5x5 r=4.0 d=3");
        double[][] t1 = generateTerrain(5, 5, 4.0, 3);
        printGrid(t1);

        // --- Generate Terrain: basic 9x9 ---
        System.out.println("\n[T6] Terrain | basic | 9x9 r=8.0 d=4");
        double[][] t2 = generateTerrain(9, 9, 8.0, 4);
        printGrid(t2);

        // --- Generate Terrain: edge case depth=0 ---
        System.out.println("\n[T7] Terrain | edge: depth=0 | 5x5 r=10.0 (all zeros)");
        double[][] t3 = generateTerrain(5, 5, 10.0, 0);
        printGrid(t3);

        // --- Generate Terrain: edge case roughness=0 ---
        System.out.println("\n[T8] Terrain | edge: roughness=0 | 5x5 d=3 (all zeros)");
        double[][] t4 = generateTerrain(5, 5, 0.0, 3);
        printGrid(t4);

        // --- Detect Artifacts: basic ---
        System.out.println("\n[T9] Artifacts | basic | grid from T6, threshold=5.0");
        List<int[]> a1 = detectArtifacts(t2, 5.0);
        System.out.println("  flagged=" + a1.size());
        printArtifacts(a1);

        // --- Detect Artifacts: flat grid ---
        System.out.println("\n[T10] Artifacts | edge: flat grid | 3x3 of 5.0, threshold=1.0");
        double[][] flat = {
                {5.0, 5.0, 5.0},
                {5.0, 5.0, 5.0},
                {5.0, 5.0, 5.0}
        };
        List<int[]> a2 = detectArtifacts(flat, 1.0);
        System.out.println("  flagged=" + a2.size() + " (expected 0)");

        // --- Detect Artifacts: extreme grid ---
        System.out.println("\n[T11] Artifacts | edge: extreme | 3x3 alternating 0/100, threshold=1.0");
        double[][] extreme = {
                {0,   100, 0},
                {100, 0,   100},
                {0,   100, 0}
        };
        List<int[]> a3 = detectArtifacts(extreme, 1.0);
        System.out.println("  flagged=" + a3.size());
        printArtifacts(a3);

        // --- Detect Artifacts: empty grid ---
        System.out.println("\n[T12] Artifacts | edge: empty grid | threshold=1.0");
        double[][] empty = new double[0][0];
        List<int[]> a4 = detectArtifacts(empty, 1.0);
        System.out.println("  flagged=" + a4.size() + " (expected 0)");

        // --- Detect Artifacts: high threshold ---
        System.out.println("\n[T13] Artifacts | edge: high threshold | grid from T6, threshold=1000.0");
        List<int[]> a5 = detectArtifacts(t2, 1000.0);
        System.out.println("  flagged=" + a5.size() + " (expected 0)");
    }
}