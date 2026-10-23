import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

os.makedirs('results/plots', exist_ok=True)
df = pd.read_csv('results/results.csv')

df['density'] = np.where(df['m'] <= 4 * df['n'], 'sparse (m=4n)', 'dense (m=20n)')


plt.figure(figsize=(10, 6))
colors = {'Kosaraju': '#1f77b4', 'Kahn': '#ff7f0e', 'DagShortestPath': '#2ca02c'}

for algo in ['Kosaraju', 'Kahn', 'DagShortestPath']:
    sub_sparse = df[(df['algorithm'] == algo) & (df['density'] == 'sparse (m=4n)')].sort_values('n')
    plt.plot(sub_sparse['n'], sub_sparse['edge_scans'], 
             marker='o', linestyle='-', color=colors[algo], 
             label=f"{algo} (sparse)")
    
    sub_dense = df[(df['algorithm'] == algo) & (df['density'] == 'dense (m=20n)')].sort_values('n')
    plt.plot(sub_dense['n'], sub_dense['edge_scans'], 
             marker='s', linestyle='--', color=colors[algo], 
             label=f"{algo} (dense)")

plt.xscale('log')
plt.yscale('log')
plt.xlabel('Number of vertices (n)')
plt.ylabel('Operations (edgeScans)')
plt.title('Operations vs n (Log-Log Scale)')
plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
plt.grid(True, which="both", ls="--", alpha=0.6)
plt.tight_layout()
plt.savefig('results/plots/operations_vs_n.png', dpi=300)
plt.close()


plt.figure(figsize=(9, 5))
kos_sparse = df[(df['algorithm'] == 'Kosaraju') & (df['density'] == 'sparse (m=4n)')].sort_values('n')
plt.plot(kos_sparse['n'], kos_sparse['edge_scans'] / kos_sparse['m'], 
         marker='o', linestyle='-', color='#1f77b4', label='Kosaraju (2 passes = 2.0)')

kahn_sparse = df[(df['algorithm'] == 'Kahn') & (df['density'] == 'sparse (m=4n)')].sort_values('n')
plt.plot(kahn_sparse['n'], kahn_sparse['edge_scans'] / kahn_sparse['m'], 
         marker='s', linestyle='--', linewidth=2.5, color='#ff7f0e', label='Kahn (1.0)')

dag_sparse = df[(df['algorithm'] == 'DagShortestPath') & (df['density'] == 'sparse (m=4n)')].sort_values('n')
plt.plot(dag_sparse['n'], dag_sparse['edge_scans'] / dag_sparse['m'], 
         marker='^', linestyle=':', linewidth=2, color='#2ca02c', label='DagShortestPath (1.0)')

plt.xscale('log')
plt.ylim(0.5, 2.5)
plt.xlabel('Number of vertices (n)')
plt.ylabel('edgeScans / m')
plt.title('Scans per edge vs n (Flat line confirms O(E) complexity)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.6)
plt.tight_layout()
plt.savefig('results/plots/scans_per_edge.png', dpi=300)
plt.close()


plt.figure(figsize=(9, 5))
kruskal_sparse = df[(df['algorithm'] == 'Kruskal') & (df['density'] == 'sparse (m=4n)')].sort_values('n')
kruskal_dense = df[(df['algorithm'] == 'Kruskal') & (df['density'] == 'dense (m=20n)')].sort_values('n')

plt.plot(kruskal_sparse['n'], kruskal_sparse['edge_scans'] / kruskal_sparse['m'], 
         marker='o', linestyle='-', color='#d62728', label='Kruskal Sparse (m = 4n)')
plt.plot(kruskal_dense['n'], kruskal_dense['edge_scans'] / kruskal_dense['m'], 
         marker='s', linestyle='--', color='#9467bd', label='Kruskal Dense (m = 20n)')

plt.xscale('log')
plt.ylim(0.0, 1.05)
plt.xlabel('Number of vertices (n)')
plt.ylabel('Examined edge ratio (edgeScans / m)')
plt.title('Share of Edges Kruskal Examines (Sparse vs Dense)')
plt.legend()
plt.grid(True, which="both", ls="--", alpha=0.6)
plt.tight_layout()
plt.savefig('results/plots/kruskal_edge_share.png', dpi=300)
plt.close()

print("All plots successfully regenerated.")