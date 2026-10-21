package com.assignment3.dsu;

public class DSU {
    private final int[] parent;
    private final int[] rank;
    private int componentsCount;
    public long findCalls = 0;
    public long successfulUnions = 0;

    public DSU(int n) {
        this.parent = new int[n];
        this.rank = new int[n];
        this.componentsCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        findCalls++;
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }

        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }

        componentsCount--;
        successfulUnions++;
        return true;
    }

    public int components() {
        return componentsCount;
    }

    public long getDsuOps() {
        return findCalls + successfulUnions;
    }
}