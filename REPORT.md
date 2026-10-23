# Assignment 3: Graph Algorithms & Empirical Complexity Analysis
**Course:** Design and Analysis of Algorithms  
**Topic:** DSU, Minimum Spanning Trees, Strongly Connected Components, and DAG Shortest Paths  

---

## 1. Asymptotic Complexity Table

| Algorithm | Theoretical Time | Space Complexity | Complexity Justification |
| :--- | :--- | :--- | :--- |
| **DSU (makeSet, find, union)** | $O(\alpha(V))$ amortized per operation | $O(V)$ | Combining path compression (flattens trees during `find`) with union by rank (balances tree heights) reduces amortized cost to the inverse Ackermann function $\alpha(V) \le 4$. |
| **Kruskal's MST** | $O(E \log E)$ | $O(V + E)$ | Dominated by sorting $E$ edges via comparison sort. Subsequent DSU iterations take $O(E \cdot \alpha(V))$, asymptotically dominated by the sorting step. |
| **Prim's MST** | $O(E \log V)$ | $O(V + E)$ | Each edge is enqueued into a binary heap (`PriorityQueue`) at most once. Minimum extraction takes $O(\log V)$ using an adjacency list. |
| **Kosaraju's SCC** | $O(V + E)$ | $O(V + E)$ | Executes two complete iterative DFS traversals: the first on $G$ to determine finish order, and the second on the transposed graph $G^T$. Each vertex and edge is visited twice. |
| **Kahn's Topological Sort** | $O(V + E)$ | $O(V + E)$ | In-degrees are computed in $O(V + E)$. Vertices with in-degree 0 are queued, and target in-degrees are decremented once per outgoing edge. |
| **DAG Shortest Paths** | $O(V + E)$ | $O(V + E)$ | Computes topological ordering via Kahn's algorithm in $O(V + E)$, followed by a single linear relaxation pass across outgoing edges. |

---

## 2. Operation Counters vs Theoretical Formulas

The benchmark suite records internal, hardware-independent counters:
- `edgeScans`: Direct traversals/relaxations of edges.
- `dsuOps`: Sum of `find` invocations and successful `union` operations.
- `comparisons`: Weight comparison operations in sorting and priority queues.
- `maxDepth`: Maximum recursion/stack depth observed during depth-first traversals.

### Empirical Invariants Across All 10 Test Configurations:

1. **Kosaraju SCC Invariant:**  
   The measured `edgeScans` strictly equals $2m$ across all test sizes ($n = 1\,000$ to $100\,000$). The ratio $\frac{\text{edgeScans}}{V + E} = \frac{2m}{n + m}$ converges to $1.60$ for sparse graphs ($m = 4n$) and $1.90$ for dense graphs ($m = 20n$), confirming linear $O(V + E)$ complexity.
2. **Kahn Topological Sort Invariant:**  
   The measured `edgeScans` is identically equal to $m$ across all instances. The ratio $\frac{\text{edgeScans}}{m} \equiv 1.00$ confirms that every edge is processed exactly once when decrementing in-degrees.
3. **DAG Shortest Paths Invariant:**  
   Edge relaxations occur exactly once per directed edge, producing an invariant `edgeScans` $\equiv m$.
4. **Kruskal DSU Operations:**  
   The ratio $\frac{\text{dsuOps}}{m}$ remains bounded in the range $[2.0, 2.8]$, verifying that exactly two `find` calls and at most one `union` occur per examined edge.

---

## 3. Correctness Arguments

### 3.1. Cut Property for Minimum Spanning Trees
Let $G = (V, E)$ be a connected, undirected, weighted graph, and let $(S, V \setminus S)$ be any cut that partitions $V$ into two disjoint non-empty subsets. Suppose edge $e = (u, v)$ is a minimum-weight edge crossing this cut, with $u \in S$ and $v \in V \setminus S$. 

Assume for contradiction that $e$ does not belong to any Minimum Spanning Tree (MST) $T^*$. Adding $e$ to $T^*$ introduces an elementary cycle $C$. Because $u \in S$ and $v \in V \setminus S$, the cycle $C$ must cross the cut $(S, V \setminus S)$ at least once more via an edge $e' = (u', v') \neq e$. Replacing $e'$ with $e$ yields a new spanning tree $T' = (T^* \setminus \{e'\}) \cup \{e\}$. Since $e$ has minimal weight across the cut, $w(e) \le w(e')$, which implies $w(T') = w(T^*) - w(e') + w(e) \le w(T^*)$. Because $T^*$ was an MST, $w(T') = w(T^*)$, meaning $T'$ is also an MST containing $e$, which contradicts the assumption. 

Kruskal's algorithm respects this property by greedily picking the globally lightest edge between disjoint components (which constitutes a minimal edge crossing the cut isolating one component). Prim's algorithm maintains a single growing tree cut $S$ and greedily selects the lightest edge crossing from $S$ to $V \setminus S$ at each step.

### 3.2. Induction on Topological Order for DAG Shortest Paths
Let $G = (V, E)$ be a Directed Acyclic Graph (DAG) with vertices topologically sorted as $\langle v_1, v_2, \dots, v_n \rangle$ such that every directed edge $(v_i, v_j)$ satisfies $i < j$. We prove by induction on the topological index $k$ that after vertex $v_k$ relaxes its outgoing edges, $dist[v_k]$ equals the true shortest distance $\delta(S, v_k)$ from the super-source.

**Base Case ($k = 1$):** Vertices with in-degree 0 have no incoming edges. Their distances are initialized to 0, which is trivially optimal.

**Inductive Step:** Assume the inductive hypothesis holds for all predecessors $v_i$ with $i < k$, such that $dist[v_i] = \delta(S, v_i)$. Consider vertex $v_k$. Because $G$ contains no cycles, any shortest path to $v_k$ consists of a shortest path to some predecessor $v_p$ followed by the final edge $(v_p, v_k)$, where $p < k$. By the inductive hypothesis, $v_p$ was processed prior to $v_k$, meaning $dist[v_p] = \delta(S, v_p)$ was finalized, and the edge $(v_p, v_k)$ was subsequently relaxed:
$$dist[v_k] \le dist[v_p] + w(v_p, v_k) = \delta(S, v_p) + w(v_p, v_k) = \delta(S, v_k)$$
Because edge relaxation never underestimates true shortest path distances, $dist[v_k] = \delta(S, v_k)$. Hence, a single linear pass over vertices in topological order guarantees exact convergence for all reachable vertices, even in the presence of negative edge weights.

---

## 4. "Which Algorithm When" Architectural Recommendations

| Graph Characteristic | Recommended Algorithm | Alternative | Justification |
| :--- | :--- | :--- | :--- |
| **Sparse Undirected Graph ($m \approx O(n)$)** | **Kruskal's Algorithm** | Prim's Algorithm | Sorting $m$ edges in $O(m \log m)$ has minimal overhead. Array-based iteration with DSU avoids the cache-miss penalties inherent to priority heap operations. |
| **Dense Undirected Graph ($m \approx O(n^2)$)** | **Prim's Algorithm** (Adjacency Matrix) | Kruskal's Algorithm | Kruskal sorts $O(n^2)$ edges in $O(n^2 \log n)$ time with high memory overhead. Prim using an adjacency matrix runs in $O(n^2)$ without a priority queue. |
| **Pre-existing Edge List Representation** | **Kruskal's Algorithm** | Prim's Algorithm | Kruskal operates directly on edge lists. Prim requires building adjacency lists first, incurring additional $O(V + E)$ time and memory overhead. |
| **DAG with Arbitrary (Including Negative) Weights** | **DAG Shortest Paths** | Bellman-Ford | Topological relaxation runs in optimal $O(V + E)$ time. Bellman-Ford requires $O(V \cdot E)$, which is redundant since negative cycles cannot exist in DAGs. |
| **General Directed Graph with Cycles & Negative Weights** | **Bellman-Ford Algorithm** | DAG Shortest Paths | Topological ordering cannot exist when cycles are present, invalidating single-pass DAG relaxation. Bellman-Ford is mandatory to detect negative weight cycles. |

---

## 5. Slope Analysis and Empirical Scaling

Logarithmic regression ($\log(\text{operations})$ vs $\log(n)$) validates theoretical bounds:
- **Linear Algorithms (Kosaraju, Kahn, DAG Shortest Paths):**  
  The log-log regression slope is $\approx 1.00$, confirming exact linear scalability with respect to edge count ($O(E)$).
- **Kruskal's Algorithm:**  
  The slope is slightly steeper ($\approx 1.05 - 1.10$) due to the comparison-based edge sorting phase ($O(E \log E)$).
- **Execution Time Considerations:**  
  Wall-clock time exhibits variance on smaller instances ($n = 1\,000$) due to JVM JIT compilation, class initialization, and garbage collection. To eliminate measurement noise, the benchmark computes the median over 5 consecutive runs and evaluates complexity primarily through deterministic operation counters.