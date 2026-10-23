package com.assignment3.mst;

import com.assignment3.core.Edge;
import com.assignment3.core.Graph;
import com.assignment3.dsu.DSU;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {
    public static class Result {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;
        public final long edgeScans;
        public final long dsuOps;
        public final long comparisons;

        public Result(long totalWeight, List<Edge> mstEdges, int components,
                      long edgeScans, long dsuOps, long comparisons) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.dsuOps = dsuOps;
            this.comparisons = comparisons;
        }
    }

    public static Result run(Graph graph) {
        long[] comparisons = new long[1];
        List<Edge> sortedEdges = new ArrayList<>(graph.edges);
        sortedEdges.sort((e1, e2) -> {
            comparisons[0]++;
            return Integer.compare(e1.weight, e2.weight);
        });

        DSU dsu = new DSU(graph.n);
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;

        for (Edge e : sortedEdges) {
            edgeScans++;
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                mstEdges.add(e);
                totalWeight += e.weight;
                if (mstEdges.size() == graph.n - 1) {
                    break;
                }
            }
        }

        return new Result(totalWeight, mstEdges, dsu.components(), edgeScans, dsu.getDsuOps(), comparisons[0]);
    }
}