import pandas as pd
import matplotlib.pyplot as plt
import os

os.makedirs('results/plots', exist_ok=True)
df = pd.read_csv('results/results.csv')

# Plot 1: Operations vs n (Log-Log)
plt.figure(figsize=(9, 5))
for algo in ['Kosaraju', 'Kahn', 'DagShortestPath']:
    subset = df[df['algorithm'] == algo]
    plt.plot(subset['n'], subset['edge_scans'], marker='o', label=f"{algo} (edgeScans)")
plt.xscale('log')
plt.yscale('log')
plt.xlabel('Number of vertices (n)')
plt.ylabel('Operations')
plt.title('Operations vs n (Log-Log Scale)')
plt.legend()
plt.grid(True, which="both", ls="--")
plt.savefig('results/plots/operations_vs_n.png', dpi=300)
plt.close()

# Plot 2: Scans per edge vs n (edgeScans / m vs n)
plt.figure(figsize=(9, 5))
for algo in ['Kosaraju', 'Kahn', 'DagShortestPath']:
    subset = df[df['algorithm'] == algo]
    plt.plot(subset['n'], subset['edge_scans'] / subset['m'], marker='s', label=f"{algo}")
plt.xscale('log')
plt.xlabel('Number of vertices (n)')
plt.ylabel('edgeScans / m')
plt.title('Scans per edge vs n (Flat line indicates O(E) complexity)')
plt.legend()
plt.grid(True)
plt.savefig('results/plots/scans_per_edge.png', dpi=300)
plt.close()

# Plot 3: Share of edges Kruskal examines (edgeScans / m)
plt.figure(figsize=(9, 5))
kruskal_df = df[df['algorithm'] == 'Kruskal']
plt.plot(kruskal_df['n'], kruskal_df['edge_scans'] / kruskal_df['m'], marker='^', color='crimson', label='Kruskal (scanned / m)')
plt.xscale('log')
plt.xlabel('Number of vertices (n)')
plt.ylabel('Examined edge ratio')
plt.title('Share of Edges Kruskal Examines')
plt.legend()
plt.grid(True)
plt.savefig('results/plots/kruskal_edge_share.png', dpi=300)
plt.close()

print("Plots successfully generated in results/plots/")
