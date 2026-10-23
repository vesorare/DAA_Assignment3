package com.assignment3;

import com.assignment3.benchmark.GraphGenerator;
import com.assignment3.core.Edge;
import com.assignment3.core.Graph;
import com.assignment3.dsu.DSU;
import com.assignment3.mst.Kruskal;
import com.assignment3.mst.Prim;
import com.assignment3.paths.DagShortestPath;
import com.assignment3.scc.Kahn;
import com.assignment3.scc.Kosaraju;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTests {

    @Test
    @DisplayName("DSU: Path compression and union-by-rank consistency")
    void testDsuOperations() {
        int n = 100;
        DSU dsu = new DSU(n);
        assertEquals(n, dsu.components());

        assertTrue(dsu.union(0, 1));
        assertEquals(n - 1, dsu.components());
        assertEquals(dsu.find(0), dsu.find(1));

        assertFalse(dsu.union(0, 1)); // Redundant union
        assertEquals(n - 1, dsu.components());
    }

    @Test
    @DisplayName("MST: Kruskal and Prim total weight agreement on 100 random graphs")
    void testMstAgreement() {
        Random rnd = new Random(1337);
        for (int i = 0; i < 100; i++) {
            int n = 30 + rnd.nextInt(30);
            int m = n + rnd.nextInt(50);
            Graph g = GraphGenerator.generateUndirected(n, m);

            Kruskal.Result kRes = Kruskal.run(g);
            Prim.Result pRes = Prim.run(g);

            assertEquals(kRes.totalWeight, pRes.totalWeight);
            assertEquals(1, kRes.components);
            assertEquals(n - 1, kRes.mstEdges.size());
        }
    }

    @Test
    @DisplayName("Topological Sort: Kahn detects cycle in cyclic graph")
    void testKahnCycleDetection() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 10);
        g.addEdge(1, 2, 20);
        g.addEdge(2, 0, 30); // forms cycle

        Kahn.Result res = Kahn.run(g);
        assertTrue(res.hasCycle);
    }

    @Test
    @DisplayName("DAG Shortest Path: Negative weights supported correctly")
    void testDagNegativeWeights() {
        Graph g = new Graph(4, true);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 10);
        g.addEdge(1, 3, -2);
        g.addEdge(2, 3, -8);

        DagShortestPath.Result res = DagShortestPath.run(g);
        assertEquals(0, res.dist[0]);
        assertEquals(5, res.dist[1]);
        assertEquals(10, res.dist[2]);
        assertEquals(2, res.dist[3]); // Path 0 -> 2 -> 3 with cost 10 - 8 = 2
    }

    @Test
    @DisplayName("Stack Safety: Kosaraju on a linear path of 100,000 vertices")
    void testKosarajuStackSafety() {
        int n = 100_000;
        Graph line = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            line.addEdge(i, i + 1, 1);
        }

        assertDoesNotThrow(() -> {
            Kosaraju.Result res = Kosaraju.run(line);
            assertEquals(n, res.sccCount);
            assertEquals(n, res.maxDepth);
        });
    }
}