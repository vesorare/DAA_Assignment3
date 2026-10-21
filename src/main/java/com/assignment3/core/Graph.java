package com.assignment3.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final boolean directed;
    public final List<List<Edge>> adj;
    public final List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            this.adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        Edge edge = new Edge(u, v, weight);
        adj.get(u).add(edge);
        edges.add(edge);
        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }

    public static Graph fromFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            do {
                line = br.readLine();
            } while (line != null && line.trim().isEmpty());

            if (line == null) {
                throw new IllegalArgumentException("File is empty: " + filePath);
            }

            String[] header = line.trim().split("\\s+");
            int n = Integer.parseInt(header[0]);
            int m = Integer.parseInt(header[1]);
            boolean directed = header[2].equalsIgnoreCase("directed");

            Graph graph = new Graph(n, directed);
            for (int i = 0; i < m; i++) {
                line = br.readLine();
                while (line != null && line.trim().isEmpty()) {
                    line = br.readLine();
                }
                if (line == null) break;

                String[] parts = line.trim().split("\\s+");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                int w = Integer.parseInt(parts[2]);
                graph.addEdge(u, v, w);
            }
            return graph;
        }
    }
}