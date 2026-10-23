package com.assignment3.mst;

import com.assignment3.core.Edge;
import com.assignment3.core.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static class Result {
        public final long totalWeight;
        public final List<Edge> mstEdges;
        public final int components;
        public final long edgeScans;
        public final long comparisons;

        public Result(long totalWeight, List<Edge> mstEdges, int components,
                      long edgeScans, long comparisons) {
            this.totalWeight = totalWeight;
            this.mstEdges = mstEdges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    public static Result run(Graph graph) {
        boolean[] visited = new boolean[graph.n];
        List<Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        long[] comparisons = new long[1];
        int components = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>((e1, e2) -> {
            comparisons[0]++;
            return Integer.compare(e1.weight, e2.weight);
        });

        // Iteration over all vertices ensures handling disconnected graphs (forest)
        for (int i = 0; i < graph.n; i++) {
            if (visited[i]) continue;
            components++;
            visited[i] = true;

            for (Edge edge : graph.adj.get(i)) {
                edgeScans++;
                pq.add(edge);
            }

            while (!pq.isEmpty()) {
                Edge minEdge = pq.poll();
                int nextVertex = minEdge.v;
                if (visited[nextVertex]) {
                    continue;
                }

                visited[nextVertex] = true;
                mstEdges.add(minEdge);
                totalWeight += minEdge.weight;

                for (Edge edge : graph.adj.get(nextVertex)) {
                    edgeScans++;
                    if (!visited[edge.v]) {
                        pq.add(edge);
                    }
                }
            }
        }

        return new Result(totalWeight, mstEdges, components, edgeScans, comparisons[0]);
    }
}