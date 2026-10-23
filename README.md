# Design and Analysis of Algorithms — Assignment 3: Graph Algorithms

A comprehensive Java graph algorithms suite implementing Disjoint Set Union (DSU), Minimum Spanning Trees (Kruskal & Prim), Strongly Connected Components (Kosaraju), Topological Sorting (Kahn), and Single-Source Shortest Paths on Directed Acyclic Graphs (DAGs).

## 1. Project Overview & Architecture

The project is structured as a standalone Maven project adhering to standard software engineering practices. All algorithms operate on a unified graph abstraction supporting both directed and undirected graphs with integer weights.

```
DAA_Assignment3/
├── pom.xml
├── README.md
├── REPORT.md
├── make_plots.py
├── data/
│   ├── graph_disconnected.txt
│   ├── graph_cyclic.txt
│   └── graph_dag.txt
├── results/
│   ├── results.csv
│   └── plots/
│       ├── operations_vs_n.png
│       ├── scans_per_edge.png
│       └── kruskal_edge_share.png
└── src/
    ├── main/java/com/assignment3/
    │   ├── core/
    │   │   ├── Edge.java             # Immutable edge representation
    │   │   └── Graph.java            # Adjacency list graph with file parser
    │   ├── dsu/
    │   │   └── DSU.java              # Disjoint Set Union (rank + path compression)
    │   ├── mst/
    │   │   ├── Kruskal.java          # Kruskal's MST algorithm with DSU
    │   │   └── Prim.java             # Prim's MST algorithm with PriorityQueue
    │   ├── scc/
    │   │   ├── Kosaraju.java         # Two-pass iterative DFS for SCCs
    │   │   └── Kahn.java             # In-degree queue-based topological sort
    │   ├── paths/
    │   │   └── DagShortestPath.java  # O(V + E) single-source DAG shortest paths
    │   └── benchmark/
    │       ├── GraphGenerator.java   # Deterministic graph generator (Random seed 42)
    │       └── BenchmarkRunner.java  # Benchmark harness verifying exact invariants
    └── test/java/com/assignment3/
        └── AlgorithmTests.java       # Comprehensive JUnit 5 test suite
```

---

## 2. Prerequisites

- **Java Development Kit (JDK):** Version 17 or higher
- **Build Tool:** Apache Maven 3.8+
- **Python Environment (for plots):** Python 3.8+ with `pandas` and `matplotlib`

---

## 3. Input File Specification

Custom graphs placed in `data/` follow the format:
- **Line 1:** `n m directed/undirected` (where `n` is vertex count, `m` is edge count).
- **Next `m` lines:** `u v w` representing an edge from `u` to `v` with integer weight `w`.
- Vertices are 0-indexed: `0 <= u, v < n`.

Example (`data/graph_dag.txt`):
```text
4 4 directed
0 1 10
0 2 5
1 3 15
2 3 8
```

---

## 4. Build, Test, and Execution Guide

### Compile the Project
```bash
mvn clean compile
```

### Run Unit Tests
Executes all unit tests, verifying DSU invariants, MST agreement between Kruskal and Prim across 100 random graphs, cycle detection, negative weight support in DAGs, and stack safety on linear graphs of 100,000 vertices:
```bash
mvn clean test
```

### Run Benchmark Suite
Executes the benchmark runner across all 10 deterministic test configurations, checks outputs against reference values, and writes `results/results.csv`:
```bash
mvn clean compile
java -cp target/classes com.assignment3.benchmark.BenchmarkRunner
```

### Generate Visualizations
Generates visualization charts in `results/plots/`:
```bash
python3 make_plots.py
```