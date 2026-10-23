package com.assignment3.scc;

import com.assignment3.core.Edge;
import com.assignment3.core.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Kahn {
    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long edgeScans;

        public Result(List<Integer> order, boolean hasCycle, long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }
    }

    public static Result run(Graph graph) {
        int n = graph.n;
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.adj.get(u)) {
                inDegree[e.v]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>(n);
        long edgeScans = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (Edge e : graph.adj.get(u)) {
                edgeScans++;
                inDegree[e.v]--;
                if (inDegree[e.v] == 0) {
                    queue.add(e.v);
                }
            }
        }

        boolean hasCycle = order.size() < n;
        return new Result(order, hasCycle, edgeScans);
    }

    public static boolean validate(Graph graph, List<Integer> order) {
        if (order.size() != graph.n) return false;
        int[] pos = new int[graph.n];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (int u = 0; u < graph.n; u++) {
            for (Edge e : graph.adj.get(u)) {
                if (pos[u] >= pos[e.v]) {
                    return false;
                }
            }
        }
        return true;
    }
}