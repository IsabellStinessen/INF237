import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/jgza5m/submissions/19306400

class Treehouses{
    static class Node{
        int id;
        double x, y;
        Node(int id, double x, double y){
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
    static class Edge implements Comparable<Edge>{
        int u, v;
        double weight;
        Edge(int u, int v, double weight){
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
        
        @Override
        public int compareTo(Edge other){
            return Double.compare(this.weight, other.weight);
        }
    }
    static class UnionFind{
        private int[] parent;
        
        public UnionFind(int n){
            parent = new int[n];
            for (int i = 0; i < n; i++){
                parent[i] = i; // initially their own parent
            }
        }
        public int find(int i){
            if(parent[i] == i){
                return i; // i == root
            }
            parent[i] = find(parent[i]); // until root is found, usually one step
            return parent[i];
        }
        public void union(int i, int j){
            int rootI = find(i);
            int rootJ = find(j);
            
            if(rootI != rootJ){
                parent[rootI] = parent[rootJ];
            }
        }
        public boolean connected(int i, int j){
            return find(i) == find(j);
        }
    }
    static double distance(Node a, Node b){
        return Math.sqrt(
            Math.pow(a.x - b.x, 2) + 
            Math.pow(a.y - b.y, 2)
        );
    }
    
    
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("graphs/treehouses/Treehouses.in"));
        
        int n = sc.nextInt();   // treehouses 1<=n<=1000
        int e = sc.nextInt();   // first e --> already connected 1<=e<=n
        int p = sc.nextInt();   // cables in place 0<=p<=1000
        
        Node[] nodes = new Node[n+1]; // index == id >= 1 (so nodes[0] never used)
        UnionFind uf = new UnionFind(n+1);
        
        // add nodes to list
        for (int i = 1; i <= n; i++){
            double x = sc.nextDouble(); // coord. for treehouse
            double y = sc.nextDouble();
            nodes[i] = new Node(i, x, y);
        }
        
        // connect first e nodes (don't need cable)
        for (int i = 2; i <= e; i++){
            uf.union(i-1, i);
        }
        // cables already in place
        for (int i = 0; i < p; i++){
            int a = sc.nextInt(); // 1. ID
            int b = sc.nextInt(); // 2. ID
            uf.union(a, b);
        }

        List<Edge> edges = new ArrayList<>();
        
        // create possible edges
        for (int i = 1; i <= n; i++){
            for (int j = i+1; j <= n; j++){
                double dis = distance(nodes[i], nodes[j]);
                edges.add(new Edge(i, j, dis));
            }
        }
        Collections.sort(edges); // sort by weight
        
        double cost = 0;
        
        // Kruskal's
        for(Edge edge : edges){
            if(!uf.connected(edge.u, edge.v)){
                uf.union(edge.u, edge.v);
                cost += edge.weight;
            }
        }
        System.out.print(cost);
        sc.close();
    }
}