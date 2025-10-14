package project2;
import java.util.*;

public class ClosestpairPoints {

    static class P {
        final double x, y; final int id;
        P(double x, double y, int id){ this.x=x; this.y=y; this.id=id; }
    }

    static double d2(P a, P b){ double dx=a.x-b.x, dy=a.y-b.y; return dx*dx+dy*dy; }

    // ---- public API: returns min distance (not square) ----
    public static double closest(List<P> pts){
        P[] px = pts.toArray(new P[0]);
        P[] py = px.clone();
        Arrays.sort(px, Comparator.comparingDouble(p->p.x));
        Arrays.sort(py, Comparator.comparingDouble(p->p.y));
        return Math.sqrt(rec(px, py));
    }

    // ---- D&C on arrays; returns distance squared ----
    private static double rec(P[] px, P[] py){
        int n = px.length;
        if(n <= 3){ // brute
            double best = Double.POSITIVE_INFINITY;
            for(int i=0;i<n;i++) for(int j=i+1;j<n;j++) best=Math.min(best, d2(px[i],px[j]));
            return best;
        }
        int mid = n/2; double midX = px[mid].x;

        // split px
        P[] pxL = Arrays.copyOfRange(px, 0, mid);
        P[] pxR = Arrays.copyOfRange(px, mid, n);

        // linear stable split of py using left id set
        HashSet<Integer> left = new HashSet<>(mid*2);
        for(P p: pxL) left.add(p.id);
        P[] pyL = new P[mid]; P[] pyR = new P[n-mid];
        int iL=0, iR=0;
        for(P p: py){ if(left.contains(p.id)) pyL[iL++]=p; else pyR[iR++]=p; }

        double dl = rec(pxL, pyL), dr = rec(pxR, pyR);
        double delta = Math.min(dl, dr), rad = Math.sqrt(delta);

        // build strip from py (already y-sorted)
        P[] strip = new P[n]; int m=0;
        for(P p: py) if(Math.abs(p.x - midX) <= rad) strip[m++] = p;

        double best = delta;
        for(int i=0;i<m;i++)
            for(int j=i+1;j<m && j<=i+7;j++) // ≤7 comparisons per point
                best = Math.min(best, d2(strip[i], strip[j]));
        return best;
    }

    // ---- tiny demo ----
    public static void main(String[] args) throws Exception {
        int minN = (args.length > 0) ? Integer.parseInt(args[0]) : 1000;
        int maxN = (args.length > 1) ? Integer.parseInt(args[1]) : 128000;
        int trials = (args.length > 2) ? Integer.parseInt(args[2]) : 7;
        String csv = (args.length > 3) ? args[3] : "closest_pair_results.csv";

        // 预热（减少JIT抖动）
        ArrayList<P> warm = new ArrayList<>();
        Random rw = new Random(1);
        for (int i = 0; i < 20000; i++) warm.add(new P(rw.nextDouble(), rw.nextDouble(), i));
        for (int i = 0; i < 3; i++) closest(warm);

        try (java.io.PrintWriter out = new java.io.PrintWriter(csv)) {
            out.println("n,trial,elapsed_nanos");
            for (int n = minN; n <= maxN; n *= 2) {
                for (int t = 1; t <= trials; t++) {
                    ArrayList<P> pts = new ArrayList<>(n);
                    Random r = new Random(); // not set seeds，promise individual within every trial
                    for (int i = 0; i < n; i++) pts.add(new P(r.nextDouble(), r.nextDouble(), i));

                    long beg = System.nanoTime();
                    double d = closest(pts); // return distance
                    long dur = System.nanoTime() - beg;

                    if (t == 1) System.out.printf("n=%d, d=%.6f, time=%.3f ms%n", n, d, dur/1e6);
                    out.printf(java.util.Locale.US, "%d,%d,%d%n", n, t, dur);
                }
            }
        }
        System.out.println("CSV written: closest_pair_results.csv");
    }
}

