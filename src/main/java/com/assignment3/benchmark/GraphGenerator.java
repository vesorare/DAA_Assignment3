package com.assignment3.benchmark;

import com.assignment3.core.Graph;
import java.util.Random;

public class GraphGenerator {
    public static Graph generateUndirected(int n, int m) {
        Random rnd = new Random(42);
        Graph graph = new Graph(n, false);

        int[] perm = new int[n];
        for (int i = 0; i < n; i++) perm[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }

        // Random spanning tree guarantees connectivity
        for (int i = 1; i < n; i++) {
            int u = perm[rnd.nextInt(i)];
            int v = perm[i];
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(u, v, w);
        }

        // Add remaining m - (n - 1) edges
        for (int i = n - 1; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, w);
        }
        return graph;
    }

    public static Graph generateDirected(int n, int m) {
        Random rnd = new Random(42);
        Graph graph = new Graph(n, true);
        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, w);
        }
        return graph;
    }

    public static Graph generateDAG(int n, int m) {
        Random rnd = new Random(42);
        Graph graph = new Graph(n, true);

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = rank[i];
            rank[i] = rank[j];
            rank[j] = tmp;
        }

        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            if (rank[a] > rank[b]) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            int w = 1 + rnd.nextInt(1000);
            graph.addEdge(a, b, w);
        }
        return graph;
    }
}