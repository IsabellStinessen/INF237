import java.util.*;
import java.io.*;

// https://uib.kattis.com/courses/INF237/spring26/assignments/t9wkpu/submissions/19524704

class Errands{
    static int n;                   // num of locations 3<=n<=100
    static Map<String, Point> map;  // map: name --> coordinates
    
    static class Point{
        double x, y;
        Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }
    static double distance(Point a, Point b){
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }
    
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(
            new FileReader("dynamic-programming/errands/Errands.in")
        );        
        n = Integer.parseInt(br.readLine().trim());  
        map = new HashMap<>();        
               
        // Get coordinates for each location in town 
        for (int i = 0; i < n; i++){
            String[] places = br.readLine().trim().split(" ");
            
            String name = places[0];
            double x = Double.parseDouble(places[1]);    // range [-100, 100]
            double y = Double.parseDouble(places[2]);
            map.put(name, new Point(x, y));
        }
        
        Point home = map.get("home");
        Point work = map.get("work");
        
        while (true){
            // Read input of places to visit on way home:
            String line = br.readLine();
            if (line == null) break;
            String[] places = line.trim().split(" ");
            int num = places.length;        // num of places to visit for each day
            
            // 1. Precompute distances:
            double[] distHome = new double[num];
            double[] distWork = new double[num];
            double[][] distBetween = new double[num][num];
            
            for (int i = 0; i < num; i++){
                Point pi = map.get(places[i]);
                distWork[i] = distance(work, pi);
                distHome[i] = distance(home, pi);
            }
            for (int i = 0; i < num; i++){
                for (int j = 0; j < num; j++){
                    Point pi = map.get(places[i]);
                    Point pj = map.get(places[j]);
                    distBetween[i][j] = distance(pi, pj);
                }
            }
                
            int maxMask = 1 << num;     
                // possible subsets of location we want to visit
            double[][] dp = new double[maxMask][num];
                // dp[mask][last] --> min cost to visit all locations in mask
                // and end in last
            int[][] parent = new int[maxMask][num];
                // stores where you came from, for path reconstruction
            
            // 2. Initialize dynamic programming:
            for (int i = 0; i < maxMask; i++){
                Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
                Arrays.fill(parent[i], -1);
            }
            
            // 3. Start from work:
            for (int i = 0; i < num; i++){
                dp[1 << i][i] = distWork[i];
            }
            
            // 4. Fill dp:
            for (int mask = 0; mask < maxMask; mask++){
                for (int last = 0; last < num; last++){
                    
                    if ((mask & (1 << last)) == 0) continue;        // last not in mask (not visited)
                    
                    for (int next = 0; next < num; next++){
                        if ((mask & (1 << next)) != 0) continue;    // only consider nodes not yet visited
                        
                        int newMask = mask | (1 << next);           // add next to visited set
                        double newCost = dp[mask][last] + distBetween[last][next];
                        
                        // if new path is better, store it
                        // update where it came from
                        if (newCost < dp[newMask][next]){
                            dp[newMask][next] = newCost;
                            parent[newMask][next] = last;
                        }
                    }
                }
            }
            
            // 5. Find best path ending, and go home
            int fullMask = maxMask - 1;         // all locations to visit as visited
            double bestCost = Double.POSITIVE_INFINITY;
            int bestLast = -1;
            
            for (int i = 0; i < num; i++){
                double totalCost = dp[fullMask][i] + distHome[i];   // total cost: full route + go home
                if (totalCost < bestCost){
                    bestCost = totalCost;
                    bestLast = i;               // find best final stop before going home
                }
            }
            
            // 6. Reconstruct path
            List<Integer> order = new ArrayList<>();
            int mask = fullMask;
            int curr = bestLast;
            
            while (curr != -1){
                order.add(curr);
                int prev = parent[mask][curr];  // use parent[][] to traverse backwards
                mask ^= (1 << curr);            // remove this location from path
                curr = prev;
            }
            
            Collections.reverse(order);
            
            // 7. Output result
            StringBuilder sb = new StringBuilder();
            for (int idx : order){
                sb.append(places[idx]).append(" ");
            }
            System.out.println(sb.toString().trim());
        }
    br.close();
    }
}