import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/jgza5m/submissions/19306279

class Main{
    static class Edge{
        int v, u, start, interval, travelTime;
        Edge(int v, int u, int start, int interval, int travelTime){
            this.v = v;
            this.u = u;
            this.start = start;
            this.interval = interval;
            this.travelTime = travelTime;
        }
    }
    public static void main(String[] args) throws FileNotFoundException{
        Scanner sc = new Scanner(new File("graphs/arriving-on-time/ArrivingOnTime.in"));
        
        int n = sc.nextInt();   // tram stops
        int m = sc.nextInt();   // tram lines
        int s = sc.nextInt();   // meeting start
        
        // Reversed graph: child --> parent
            // For going backwards later
        ArrayList<Edge>[] revGraph = new ArrayList[n];
        for (int i = 0; i < n; i++){
            revGraph[i] = new ArrayList<>();
        }
        
        for (int i = 0; i < m; i++){
            int u = sc.nextInt();   // start stop
            int v = sc.nextInt();   // end stop
            int t = sc.nextInt();   // start time from now
            int p = sc.nextInt();   // depart interval
            int d = sc.nextInt();   // time from depart to arrive
            revGraph[v].add(new Edge(u, v, t, p, d));
        }

        int[] latestArrival = new int[n];
        Arrays.fill(latestArrival, -1);
        latestArrival[n-1] = s;
        
        Queue<Integer> q = new LinkedList<>();
        q.add(n-1);
        
        // going backwards from n-1 to 0, saving latest possible arrival to stop
        while(!q.isEmpty()){
            int u = q.poll(); // end point, v --> u
            
            for (Edge e : revGraph[u]){
                int v = e.v; // parent point
                
                int L = latestArrival[u];
                int lastDeparture;
                if (L < e.start + e.travelTime) continue;  // already saved later arrival
                if (e.interval == 0) lastDeparture = e.start;
                else {
                    int k = (L - e.travelTime - e.start) / e.interval;
                    lastDeparture = e.start + k * e.interval;
                }
                if(lastDeparture > latestArrival[v]){
                    latestArrival[v] = lastDeparture;
                    q.add(v);
                }
            }
        }
        if (latestArrival[0] == -1){
            System.out.print("impossible");
        } else System.out.print(latestArrival[0]);
        sc.close();
    }
}