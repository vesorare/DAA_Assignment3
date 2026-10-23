package com.assignment3.benchmark;

import com.assignment3.core.Graph;
import com.assignment3.mst.Kruskal;
import com.assignment3.mst.Prim;
import com.assignment3.paths.DagShortestPath;
import com.assignment3.scc.Kahn;
import com.assignment3.scc.Kosaraju;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class BenchmarkRunner {
    private static final int[][] SIZES = {
            {1000, 4000},
            {1000, 20000},
            {3162, 12648},
            {3162, 63240},
            {10000, 40000},
            {10000, 200000},
            {31623, 126492},
            {31623, 632460},
            {100000, 400000},
            {100000, 2000000}
    };

    private static final long[] REF_MST = {
            147970L, 30363L, 468911L, 97589L, 1486139L,
            309550L, 4715515L, 968517L, 15012094L, 3056681L
    };

    private static final int[] REF_SCC = {
            27, 1, 137, 1, 373, 1, 1188, 1, 3923, 1
    };

    private static final long[] REF_DAG_SUM = {
            396274L, 252411L, 1271911L, 723157L, 3941828L,
            2381342L, 12551891L, 7376612L, 40096539L, 23512087L
    };

    public static void main(String[] args) throws IOException {
        File dir = new File("results");
        if (!dir.exists()) dir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter("results/results.csv"))) {
            pw.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");

            for (int idx = 0; idx < SIZES.length; idx++) {
                int n = SIZES[idx][0];
                int m = SIZES[idx][1];
                String graphLabel = n + "/" + m;

                System.out.printf("Running benchmark for size n=%d, m=%d...%n", n, m);

                // 1. Kruskal & Prim on Undirected Graph
                Graph undir = GraphGenerator.generateUndirected(n, m);

                // Kruskal
                long[] tKruskal = new long[5];
                Kruskal.Result kRes = null;
                for (int r = 0; r < 5; r++) {
                    long st = System.nanoTime();
                    kRes = Kruskal.run(undir);
                    tKruskal[r] = (System.nanoTime() - st) / 1_000_000;
                }
                Arrays.sort(tKruskal);
                long medKruskal = tKruskal[2];
                if (kRes.totalWeight != REF_MST[idx]) {
                    throw new AssertionError("Kruskal weight mismatch at " + graphLabel + ": expected " + REF_MST[idx] + " got " + kRes.totalWeight);
                }
                pw.printf("Kruskal,%s,%d,%d,%d,%d,%d,%d,0,%d%n",
                        graphLabel, n, m, medKruskal, kRes.dsuOps, kRes.edgeScans, kRes.comparisons, kRes.totalWeight);

                // Prim
                long[] tPrim = new long[5];
                Prim.Result pRes = null;
                for (int r = 0; r < 5; r++) {
                    long st = System.nanoTime();
                    pRes = Prim.run(undir);
                    tPrim[r] = (System.nanoTime() - st) / 1_000_000;
                }
                Arrays.sort(tPrim);
                long medPrim = tPrim[2];
                if (pRes.totalWeight != kRes.totalWeight) {
                    throw new AssertionError("Prim totalWeight does not match Kruskal at " + graphLabel);
                }
                pw.printf("Prim,%s,%d,%d,%d,0,%d,%d,0,%d%n",
                        graphLabel, n, m, medPrim, pRes.edgeScans, pRes.comparisons, pRes.totalWeight);

                // 2. Kosaraju on Directed Graph
                Graph dirGraph = GraphGenerator.generateDirected(n, m);
                long[] tKos = new long[5];
                Kosaraju.Result kosRes = null;
                for (int r = 0; r < 5; r++) {
                    long st = System.nanoTime();
                    kosRes = Kosaraju.run(dirGraph);
                    tKos[r] = (System.nanoTime() - st) / 1_000_000;
                }
                Arrays.sort(tKos);
                long medKos = tKos[2];
                if (kosRes.sccCount != REF_SCC[idx]) {
                    throw new AssertionError("Kosaraju SCC count mismatch at " + graphLabel + ": expected " + REF_SCC[idx] + " got " + kosRes.sccCount);
                }
                if (kosRes.edgeScans != 2L * m) {
                    throw new AssertionError("Kosaraju edgeScans invariant violated: " + kosRes.edgeScans + " != " + (2L * m));
                }
                pw.printf("Kosaraju,%s,%d,%d,%d,0,%d,0,%d,%d%n",
                        graphLabel, n, m, medKos, kosRes.edgeScans, kosRes.maxDepth, kosRes.sccCount);

                // 3. Kahn on DAG
                Graph dag = GraphGenerator.generateDAG(n, m);
                long[] tKahn = new long[5];
                Kahn.Result kahnRes = null;
                for (int r = 0; r < 5; r++) {
                    long st = System.nanoTime();
                    kahnRes = Kahn.run(dag);
                    tKahn[r] = (System.nanoTime() - st) / 1_000_000;
                }
                Arrays.sort(tKahn);
                long medKahn = tKahn[2];
                if (kahnRes.hasCycle || !Kahn.validate(dag, kahnRes.order)) {
                    throw new AssertionError("Kahn invalid order or detected false cycle at " + graphLabel);
                }
                if (kahnRes.edgeScans != m) {
                    throw new AssertionError("Kahn edgeScans invariant violated: " + kahnRes.edgeScans + " != " + m);
                }
                pw.printf("Kahn,%s,%d,%d,%d,0,%d,0,0,%d%n",
                        graphLabel, n, m, medKahn, kahnRes.edgeScans, kahnRes.order.size());

                // 4. DAG Shortest Path
                long[] tDag = new long[5];
                DagShortestPath.Result dagRes = null;
                for (int r = 0; r < 5; r++) {
                    long st = System.nanoTime();
                    dagRes = DagShortestPath.run(dag);
                    tDag[r] = (System.nanoTime() - st) / 1_000_000;
                }
                Arrays.sort(tDag);
                long medDag = tDag[2];
                if (dagRes.sumDistances != REF_DAG_SUM[idx]) {
                    throw new AssertionError("DAG distance sum mismatch at " + graphLabel + ": expected " + REF_DAG_SUM[idx] + " got " + dagRes.sumDistances);
                }
                if (dagRes.edgeScans != m) {
                    throw new AssertionError("DAG Shortest Paths edgeScans invariant violated: " + dagRes.edgeScans + " != " + m);
                }
                pw.printf("DagShortestPath,%s,%d,%d,%d,0,%d,0,0,%d%n",
                        graphLabel, n, m, medDag, dagRes.edgeScans, dagRes.sumDistances);
            }
        }
        System.out.println("Benchmark completed successfully. Results saved to results/results.csv");
    }
}