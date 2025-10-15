package project2;

import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClosestpairPoints.closest(...)
 * Checks correctness against hand-crafted cases and an O(n^2) brute force.
 */
public class ClosestpairPointsTest {

    // ---------- helpers ----------

    /** Euclidean distance (not squared) */
    static double dist(ClosestpairPoints.P a, ClosestpairPoints.P b) {
        double dx = a.x - b.x, dy = a.y - b.y;
        return Math.hypot(dx, dy);
    }

    /** Brute-force closest distance for small n (O(n^2)) */
    static double brute(List<ClosestpairPoints.P> pts) {
        double best = Double.POSITIVE_INFINITY;
        int n = pts.size();
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                best = Math.min(best, dist(pts.get(i), pts.get(j)));
        return best;
    }

    /** Generate n random points in [0,1)^2 with fixed seed */
    static ArrayList<ClosestpairPoints.P> randomPoints(int n, long seed) {
        Random r = new Random(seed);
        ArrayList<ClosestpairPoints.P> pts = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            pts.add(new ClosestpairPoints.P(r.nextDouble(), r.nextDouble(), i));
        return pts;
    }

    // ---------- tests ----------

    @Test
    void testSmallHandcrafted() {
        // A tiny set where the closest pair is obvious: (0,0) ~ (0,0.1)
        List<ClosestpairPoints.P> pts = List.of(
                new ClosestpairPoints.P(0.0, 0.0, 0),
                new ClosestpairPoints.P(0.0, 0.1, 1),
                new ClosestpairPoints.P(0.9, 0.9, 2),
                new ClosestpairPoints.P(0.5, 0.7, 3)
        );
        double ans = ClosestpairPoints.closest(pts);
        assertEquals(0.1, ans, 1e-12, "closest distance should be 0.1");
    }

    @Test
    void testRandomCompareBrute_ManySeeds() {
        // Compare to brute force on small n and many seeds
        int[] ns = {2, 3, 5, 10, 20, 50, 100, 150, 200};
        double eps = 1e-9; // double 精度容差
        for (int n : ns) {
            for (long seed = 1; seed <= 100; seed++) {
                ArrayList<ClosestpairPoints.P> pts = randomPoints(n, seed);
                double d1 = ClosestpairPoints.closest(pts);
                double d2 = brute(pts);
                assertEquals(d2, d1, eps, "mismatch at n=" + n + " seed=" + seed);
            }
        }
    }

    @Test
    void testOrderInvariance() {
        // Shuffling the input should not change the answer
        ArrayList<ClosestpairPoints.P> pts = randomPoints(200, 42L);
        double d1 = ClosestpairPoints.closest(pts);

        Collections.shuffle(pts, new Random(7));
        double d2 = ClosestpairPoints.closest(pts);

        assertEquals(d1, d2, 1e-12, "result must be invariant to input order");
    }
}

