import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/t3ncgx/submissions/19557505

/*
    Max flow - Edmonds-Karp (BFS-based)
        - Build graph with edges between rooms that have GCD > 1
        - Source = room with lowest number, Sink = room with highest number
        - Edge capacity = GCD of the two rooms
*/

class MazeMovement{
    static int n;                   // num of rooms
    static int[] rooms;             // room numbers
    static List<Edge>[] graph;      // adjacency list
    
    static class Edge{
        int to, cap;
        Edge rev;                   // reverse edge for residual graph
        Edge(int to, int cap){
            this.to = to;
            this.cap = cap;
        }
    }
    
    // Find Greatest Common Divisor: edge exists if GCD > 1
    static int GCD(int a, int b){
        while (b != 0){
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Add edge
    static void addEdge(int from, int to, int cap){
        Edge a = new Edge(to, cap);
        Edge b = new Edge(from, 0); // reverse edge with 0 capacity
        a.rev = b;
        b.rev = a;
        graph[from].add(a);
        graph[to].add(b);
    }

    /*
        Edmonds-Karp algortihm:

        Repeatadly:
            1. Find shortest path from source to sink using BFS
            2. Push as much flow as possible through that path (bottleneck capacity)
            3. Update residual graph
    */
    static int maxFlow(int s, int t){
        int flow = 0;

        while (true){
            /*
                parent[v] --> edge used to reach node v
                parentNode[v] --> previous node in path to v
            */
            Edge[] parent = new Edge[n];
            int[] parentNode = new int[n];
            Arrays.fill(parentNode, -1);

            Queue<Integer> q = new ArrayDeque<>();
            q.add(s);
            parentNode[s] = s; // source visited

            // BFS: shortest augmenting path
            while (!q.isEmpty() && parentNode[t] == -1){
                int u = q.poll();

                for (Edge e : graph[u]){
                    if (parentNode[e.to] == -1 && e.cap > 0){ // not visited and has cap
                        parentNode[e.to] = u;
                        parent[e.to] = e;       // store edge leading to this node
                        q.add(e.to);

                        if (e.to == t) break;   // reached sink
                    }
                }
            }

            if (parentNode[t] == -1) break;     // no path from s to t

            // Find bottleneck capacity --> min residual capacity along the path from s to t
            int bottleneck = Integer.MAX_VALUE;
            int v = t;
            while (v != s){
                bottleneck = Math.min(bottleneck, parent[v].cap);
                v = parentNode[v];
            }

            // Push flow through the path, and update residual graph
            v = t;
            while (v != s){
                Edge e = parent[v];
                e.cap -= bottleneck;            // reduce cap on forward edge
                e.rev.cap += bottleneck;        // increase cap on reverse edge (residual)
                v = parentNode[v];
            }
            flow += bottleneck;
        }
        return flow;
    }

    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("network-flow/maze-movement/MazeMovement.in")
        ); 

        n = Integer.parseInt(br.readLine().trim());
        rooms = new int[n];
        
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        int s = -1, t = -1;
            // lowest room number --> souce (s)
            // highest room number --> sink (t)
        
        // Read input + find source and sink
        for (int i = 0; i < n; i++){
            rooms[i] = Integer.parseInt(br.readLine().trim());

            if (rooms[i] < minVal) {
                minVal = rooms[i];
                s = i;
            }
            if (rooms[i] > maxVal) {
                maxVal = rooms[i];
                t = i;
            }
        }

        // Build graph
        for (int i = 0; i < n-1; i++){
            for (int j = i+1; j < n; j++){
                int capacity = GCD(rooms[i], rooms[j]);

                if (capacity > 1){
                    addEdge(i, j, capacity);
                    addEdge(j, i, capacity);
                }
            }
        }

        int result = maxFlow(s, t);
        System.out.println(result);
        br.close();
    }
}