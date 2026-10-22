package com.assignment3.scc;

import com.assignment3.core.Edge;
import com.assignment3.core.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kosaraju {
    public static class Result {
        public final int sccCount;
        public final int[] sccIds;
        public final long edgeScans;
        public final int maxDepth;

        public Result(int sccCount, int[] sccIds, long edgeScans, int maxDepth) {
            this.sccCount = sccCount;
            this.sccIds = sccIds;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }
    }

    public static Result run(Graph graph) {
        int n = graph.n;
        long edgeScans = 0;
        int maxDepth = 0;

        // Construct reversed graph
        List<List<Edge>> revAdj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) revAdj.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.adj.get(u)) {
                revAdj.get(e.v).add(new Edge(e.v, e.u, e.weight));
            }
        }

        // Pass 1: Explicit iterative DFS to obtain finish order
        boolean[] visited = new boolean[n];
        int[] edgeIdx = new int[n];
        int[] stack = new int[n];
        int top = -1;

        int[] finishOrder = new int[n];
        int finishPtr = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                stack[++top] = i;
                visited[i] = true;
                if (top + 1 > maxDepth) maxDepth = top + 1;

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> neighbors = graph.adj.get(u);
                    if (edgeIdx[u] < neighbors.size()) {
                        Edge e = neighbors.get(edgeIdx[u]++);
                        edgeScans++;
                        int v = e.v;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack[++top] = v;
                            if (top + 1 > maxDepth) maxDepth = top + 1;
                        }
                    } else {
                        top--;
                        finishOrder[finishPtr++] = u;
                    }
                }
            }
        }

        // Pass 2: Iterative DFS on reversed graph in decreasing finish order
        Arrays.fill(visited, false);
        Arrays.fill(edgeIdx, 0);
        top = -1;

        int sccCount = 0;
        int[] sccIds = new int[n];

        for (int i = n - 1; i >= 0; i--) {
            int start = finishOrder[i];
            if (!visited[start]) {
                sccCount++;
                stack[++top] = start;
                visited[start] = true;
                sccIds[start] = sccCount;
                if (top + 1 > maxDepth) maxDepth = top + 1;

                while (top >= 0) {
                    int u = stack[top];
                    List<Edge> revNeighbors = revAdj.get(u);
                    if (edgeIdx[u] < revNeighbors.size()) {
                        Edge e = revNeighbors.get(edgeIdx[u]++);
                        edgeScans++;
                        int v = e.v;
                        if (!visited[v]) {
                            visited[v] = true;
                            sccIds[v] = sccCount;
                            stack[++top] = v;
                            if (top + 1 > maxDepth) maxDepth = top + 1;
                        }
                    } else {
                        top--;
                    }
                }
            }
        }

        return new Result(sccCount, sccIds, edgeScans, maxDepth);
    }
}