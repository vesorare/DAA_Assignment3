package com.assignment3.paths;

import com.assignment3.core.Edge;
import com.assignment3.core.Graph;
import com.assignment3.scc.Kahn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagShortestPath {
    public static class Result {
        public final long[] dist;
        public final int[] parent;
        public final long sumDistances;
        public final long edgeScans;

        public Result(long[] dist, int[] parent, long sumDistances, long edgeScans) {
            this.dist = dist;
            this.parent = parent;
            this.sumDistances = sumDistances;
            this.edgeScans = edgeScans;
        }
    }

    public static Result run(Graph graph) {
        Kahn.Result kahnRes = Kahn.run(graph);
        if (kahnRes.hasCycle) {
            throw new IllegalArgumentException("Graph contains cycles; DAG shortest paths requires a DAG.");
        }

        int n = graph.n;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Long.MAX_VALUE / 2);
        Arrays.fill(parent, -1);

        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.adj.get(u)) {
                inDegree[e.v]++;
            }
        }

        // Virtual super-source: all inDegree == 0 vertices start with distance 0
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        long edgeScans = 0;
        for (int u : kahnRes.order) {
            if (dist[u] == Long.MAX_VALUE / 2) continue;
            for (Edge e : graph.adj.get(u)) {
                edgeScans++;
                if (dist[u] + e.weight < dist[e.v]) {
                    dist[e.v] = dist[u] + e.weight;
                    parent[e.v] = u;
                }
            }
        }

        long sumDistances = 0;
        for (int i = 0; i < n; i++) {
            sumDistances += dist[i];
        }

        return new Result(dist, parent, sumDistances, edgeScans);
    }

    public static List<Integer> getPathTo(int target, int[] parent) {
        List<Integer> path = new ArrayList<>();
        for (int at = target; at != -1; at = parent[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}