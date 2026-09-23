import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/t3ncgx/submissions/19557473

/*
    Dinic's max flow algorithm for min cut
        - Each cell is split into in-node and out-node, connected by an edge with capacity = cost of barricading that cell
        - Edges from out-node to in-node of adjacent cells with infinite capacity (can move freely between them)
        - Super source connected to B's in-node with infinite capacity
        - Out-nodes of border cells connected to super sink with infinite capacity
*/

class CopsAndRobbers{
    
    static char[][] grid;
    static long[] cost;
    static final long INF = (long)1e15; // used as "infinite value" to not cause overflow when summing MAX_VALUE

    static class Edge{
        int to, rev;
        long cap;       // remaining capacity 
        Edge(int to, int rev, long cap){
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    /*
        Dinic's max flow algorithm:
            - BFS to build level graph
            - DFS to send flow while respecting levels
    */
    static class Dinic{
        List<Edge>[] graph;
        int[] level;        // distance from source/B (for BFS)
        int[] pointer;      // edges we have tried (for DFS)
        int n;              // nodes in graph

        Dinic(int n){
            this.n = n;
            graph = new ArrayList[n];
            for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
            level = new int[n];
            pointer = new int[n];
        }

        // Add directed edge u --> v, with capacity
        // Also add reverse edge v --> u with 0 capacity for residual graph
        void addEdge(int u ,int v, long cap){
            graph[u].add(new Edge(v, graph[v].size(), cap));
            graph[v].add(new Edge(u, graph[u].size() - 1, 0)); // reverse edge
        }

        // BFS to build level graph, returns true if sink is reachable from source
        boolean bfs(int s, int t){
            Arrays.fill(level, -1);
            Queue<Integer> q = new ArrayDeque<>();
            level[s] = 0;
            q.add(s);

            while (!q.isEmpty()){
                int u = q.poll();
                for (Edge e : graph[u]){
                    // If edge has remaining capacity and destination not visited
                    if (e.cap > 0 && level[e.to] == -1){ 
                        level[e.to] = level[u] + 1;
                        q.add(e.to);
                    }
                }
            }
            return level[t] != -1; // return true if sink is reachable --> we can still push flow
        }

        // DFS: push flow through level graph, returns flow pushed
        long dfs(int u, int t, long flow){
            if (u == t || flow == 0) return flow;   // reached sink or no flow to send

            // Try adj edges from current pointer
            for (; pointer[u] < graph[u].size(); pointer[u]++){
                Edge e = graph[u].get(pointer[u]);

                // If edge has capacity and leads to next level
                if (e.cap > 0 && level[e.to] == level[u] + 1){
                    long pushed = dfs(e.to, t, Math.min(flow, e.cap));
                    if (pushed > 0){
                        e.cap -= pushed;                      // reduce cap on forward edge
                        graph[e.to].get(e.rev).cap += pushed; // increase cap on reverse edge
                        return pushed;
                    }
                }
            }
            return 0; // no flow pushed from this node
        }

        // Main function to calculate max flow from s to t
        long maxFlow(int s, int t){
            if (s == t) return -1; // cannot block if source and sink are the same

            long flow = 0;

            // Repeat while we can still reach sink
            while (bfs(s, t)){
                Arrays.fill(pointer, 0); // reset pointers for DFS
                long pushed;
                while ((pushed = dfs(s, t, INF)) > 0){
                    flow += pushed;
                }
            }
            return flow;
        }
    }

    // Node splitting: in and out nodes for each cell
    static int in(int i, int j){
        return (i * grid[0].length + j) * 2;
    }
    static int out(int i, int j){
        return (i * grid[0].length + j) * 2 + 1;
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("network-flow/cops-and-robbers/CopsAndRobbers.in")
        ); 

        String[] parts = br.readLine().trim().split(" ");
        
        int cols = Integer.parseInt(parts[0]); 
        int rows = Integer.parseInt(parts[1]);
        int c = Integer.parseInt(parts[2]);     // num of terrain types
        
        grid = new char[rows][cols];
        cost = new long[c];

        int bx = -1, by = -1;        

        // Read grid and find B's position
        for (int i = 0; i < rows; i++){
            char[] cells = br.readLine().trim().toCharArray();
            for (int j = 0; j < cols; j++){
                grid[i][j] = cells[j];
                if (grid[i][j] == 'B'){
                    bx = i;
                    by = j;
                }
            }
        }
        
        // Read terrain costs
        String[] terrains = br.readLine().trim().split(" ");
        for (int i = 0; i < c; i++){
            long iCost = Long.parseLong(terrains[i]);
            cost[i] = iCost;
        }

        // Node indexing --> all cells get in and out nodes
        int totalNodes = rows * cols * 2 + 2; // 2 per cell (in and out) + super source + super sink
        int source = totalNodes -2;  // super source for B's cell
        int sink = totalNodes - 1;   // super sink for border cells

        Dinic dinic = new Dinic(totalNodes);

        // Directions for neighbours (up, down, left, right)
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        // BUILD GRAPH:
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                char ch = grid[i][j];

                // 1. node splitting
                long cap;
                if (ch == 'B' || ch == '.'){
                    cap = Long.MAX_VALUE; // cannot block: infinite capacity 
                } else {
                    cap = cost[ch - 'a']; // cost of barricading this cell
                }
                // in -> out edge with capacity = cost of blocking that cell
                dinic.addEdge(in(i, j), out(i, j), cap);

                // 2. edges to neighbours (out-node to in-node)
                for (int d = 0; d < 4; d++){
                    int ni = i + dx[d];
                    int nj = j + dy[d];

                    if (ni >= 0 && ni < rows && nj >= 0 && nj < cols){
                        dinic.addEdge(out(i, j), in(ni, nj), INF); // infinite capacity between neighbours
                    } 
                }

                // 3. If border cell, connect out-node to super sink
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1){
                    dinic.addEdge(out(i, j), sink, INF); // infinite capacity to sink
                }
            }
        }

        // 4. Connect source to B's in-node
        dinic.addEdge(source, in(bx, by), INF);
        
        
        // Compute max flow = min cut
        long result = dinic.maxFlow(source, sink);

        System.out.println(result == INF ? -1 : result);
        br.close();
    }
}